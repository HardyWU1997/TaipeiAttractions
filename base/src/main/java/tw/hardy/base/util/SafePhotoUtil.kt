package tw.hardy.base.util

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import tw.hardy.base.App
import tw.hardy.base.data.local.SharePreference
import java.io.File
import java.io.FileOutputStream

/**
 * 拍照永不丟失工具類
 */
class SafePhotoUtil(
    // activity or fragment
    private val activityOrFragment: Any,
) {
    private var photoRequest: PhotoRequest? = null

    private val sharedFlow = MutableSharedFlow<Status>()

    // 拍照的 content
    private val takePictureContent = when (activityOrFragment) {
        is Fragment -> activityOrFragment.registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ::handleTakePhotoResult
        )
        is FragmentActivity -> activityOrFragment.registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            ::handleTakePhotoResult
        )
        else -> throw Exception("holder is not FragmentActivity or Fragment.")
    }

    // 相簿獲取照片的 content
    private val getMultiPictureContent = when (activityOrFragment) {
        is Fragment -> activityOrFragment.registerForActivityResult(
            ActivityResultContracts.GetMultipleContents(),
            ::handleGetPhotoResult
        )
        is FragmentActivity -> activityOrFragment.registerForActivityResult(
            ActivityResultContracts.GetMultipleContents(),
            ::handleGetPhotoResult
        )
        else -> throw Exception("holder is not FragmentActivity or Fragment.")
    }

    /**
     * 擋 savedInstanceState 有值時，說明是系統重新創建的，此時發送 cache 住的資料
     * 否則移除 cache 住的資料
     */
    suspend fun startCollect(
        savedInstanceState: Bundle?,
        action: (value: Status) -> Unit,
    ) {
        if (savedInstanceState == null) {
            clearCache()
        } else {
            compressThenEmitPhotos()
        }
        sharedFlow.collect(action)
    }


    fun clearCache() {
        CoroutineScope(Dispatchers.IO).launch {
            photoRequest = null
            SharePreference.storeString(TakePictureCacheKey, "")
            SharePreference.storeString(PhotoRequestKey, "")
            App.getApplication().deleteAllTempPhoto()
        }
    }

    fun deletePhoto(path: String) {
        val file = File(path)
        file.delete()
        file.parentFile?.name?.let {
            CoroutineScope(Dispatchers.IO).launch {
                emitRequestTypesPhoto(it)
            }
        }
    }

    fun getPicture(
        requestType: String = DefaultRequestType,
        maxPhotoNumber: Int = 1
    ) = getPictureInternal(requestType, maxPhotoNumber)

    /**
     * 檢查 permission 後，選擇模式「拍照」 or 「手機相簿」
     */
    private fun getPictureInternal(requestType: String, maxPhotoNumber: Int) =
        SimplePermission.checkSuccess(activityOrFragment, Manifest.permission.CAMERA) {
            App.getApplication().let {
                val photoNumber = it.getChildDirSizeInTemp(requestType)
                if (photoNumber >= maxPhotoNumber) {
                    it.showDialog("照片已超過${maxPhotoNumber}張")
                    return@checkSuccess
                }
                photoRequest = PhotoRequest(requestType, maxPhotoNumber).also { photoRequest ->
                    SharePreference.storeObject(PhotoRequestKey, photoRequest)
                }
                selectMode(requestType)
            }
        }

    enum class PhotoMode(val mode: String) {
        CAMERA("拍照模式"), ALBUM("手機相簿")
    }

    private fun selectMode(requestType: String) =
        getContext()?.showListDialog(list = listOf(PhotoMode.CAMERA.mode, PhotoMode.ALBUM.mode))
        { _, text ->
            when (text) {
                PhotoMode.CAMERA.mode -> modeTakePhoto(requestType)
                PhotoMode.ALBUM.mode -> modeGetPhoto()
            }
        }

    private fun modeTakePhoto(requestType: String) {
        val context = App.getApplication()
        context.createPhotoInChildDir(childDirName = requestType)?.let {
            cacheUnCompressedPhoto(listOf(it.absolutePath))
            takePictureContent.launch(FileProvider.getUriForFile(context, AUTHORITY, it))
        } ?: context.showDialog("拍照失敗，請重試")
    }

    private fun modeGetPhoto() {
        getMultiPictureContent.launch("image/*")
    }

    /**
     * 處理拍照返回結果
     */
    private fun handleTakePhotoResult(isSuccess: Boolean) {
        if (isSuccess) {
            compressThenEmitPhotos()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                sharedFlow.emit(Status.Fail)
            }
        }
    }

    /**
     * 處理獲取相簿的返回結果
     */
    private fun handleGetPhotoResult(uriList: List<Uri>) {
        if (uriList.isEmpty()) {
            return
        }
        val context = App.getApplication()
        val photoRequest =
            photoRequest ?: SharePreference.fetchObject<PhotoRequest>(PhotoRequestKey)
                ?.apply {
                    photoRequest = this
                } ?: let {
                context.showDialog("獲取照片失敗！請重試")
                return
            }
        SharePreference.storeString(PhotoRequestKey, "")
        val filterList = mutableListOf<Uri>()
        uriList.forEach {
            if (filterList.size == photoRequest.maxPhotoNumber) {
                return@forEach
            }
            filterList.add(it)
        }
        compressThenEmitPhotos {
            cacheUnCompressedPhoto(
                filterList.toFilePathList(
                    childDirName = photoRequest.requestType,
                    context = context
                )
            )
        }
    }


    //  ---------------------------------------------------------------- 一些額外的操作

    /**
     * 壓縮處理後，發送圖片。
     */
    private fun compressThenEmitPhotos(before: () -> Unit = {}) {
        val context = App.getApplication()
        CoroutineScope(Dispatchers.IO).launch {
            sharedFlow.emit(Status.StartLoading)
            before.invoke()
            compressCachePhoto(context)
            photoRequest?.requestType?.let { requestType ->
                emitRequestTypesPhoto(requestType)
            } ?: emitAllTypePhoto()
        }
    }

    private suspend fun emitRequestTypesPhoto(requestType: String) {
        App.getApplication().getPhotosInChildDir(childDirName = requestType)?.let { fileList ->
            sharedFlow.emit(Status.Success(requestType, fileList))
        } ?: sharedFlow.emit(Status.Fail)
    }

    private suspend fun emitAllTypePhoto() {
        val context = App.getApplication()
        context.getAllChildDirPath()?.forEach {
            val requestType = it.name
            context.getPhotosInChildDir(childDirName = requestType)?.let { fileList ->
                sharedFlow.emit(Status.Success(requestType, fileList))
            } ?: sharedFlow.emit(Status.Fail)
        } ?: sharedFlow.emit(Status.Fail)
    }


    /**
     * 還沒壓縮的照片的路徑保存起來
     */
    private fun cacheUnCompressedPhoto(fileList: List<String>) {
        val cacheList =
            SharePreference.fetchObject<ArrayList<String>>(TakePictureCacheKey) ?: ArrayList()
        cacheList.addAll(fileList)
        SharePreference.storeObject(TakePictureCacheKey, cacheList)
    }


    /**
     * 壓縮所有還沒壓縮過的圖片(發送前都會檢查一遍)
     */
    private suspend fun compressCachePhoto(context: Context) {
        SharePreference.fetchObject<ArrayList<String>>(TakePictureCacheKey)?.map {
            File(it)
        }?.forEach { file ->
            kotlin.runCatching {
                file.compress(context)
            }.onFailure {
                deleteFileByPath(file.absolutePath)
            }
        }

        SharePreference.storeString(TakePictureCacheKey, "")
    }

    private fun getContext(): Context? = when (activityOrFragment) {
        is Fragment -> activityOrFragment.context
        is FragmentActivity -> activityOrFragment
        else -> null
    }


    fun List<Uri>.toFilePathList(childDirName: String, context: Context): List<String> {
        val returnList = ArrayList<String>()
        this.forEach {
            val filePath = it.toPhotoFile(childDirName, context)?.absolutePath
            if (filePath != null) {
                returnList.add(filePath)
            }
        }
        return returnList
    }

    fun Uri.toPhotoFile(childDirName: String, context: Context): File? {
        kotlin.runCatching {
            context.createPhotoInChildDir(childDirName)?.let { file ->
                context.contentResolver.openInputStream(this)?.apply {
                    val fos = FileOutputStream(file)
                    copyTo(fos)
                    kotlin.runCatching {
                        close()
                        fos.close()
                    }
                    return file
                }
            }
        }
        return null
    }


    private fun Context.getAllChildDirPath() = getTempDirPath()?.let {
        getFileListByPath(it)
    }

    private fun Context.deleteAllTempPhoto() = getTempDirPath()?.let { deleteFileByPath(it) }

    private fun Context.getTempDirPath() = getDirPathInType(
        dirName = photoDir,
        type = Environment.DIRECTORY_PICTURES
    )

    private fun Context.getChildDirSizeInTemp(childDirName: String): Int =
        getChildDirInTemp(childDirName)?.let {
            File(it).listFiles()?.size
        } ?: 0

    private fun Context.getChildDirInTemp(childDirName: String): String? = getDirPathInType(
        dirName = photoDir,
        type = Environment.DIRECTORY_PICTURES
    )?.let { tempDirPath ->
        getDirPath(tempDirPath, childDirName)
    }

    private fun Context.createPhotoInChildDir(childDirName: String): File? =
        getChildDirInTemp(childDirName)?.let { childDirPath ->
            createFileInDir(jpgSuffix, childDirPath)
        }

    private fun Context.getPhotosInChildDir(childDirName: String) =
        getChildDirInTemp(childDirName)?.let { tempFilePath ->
            getFileListByPath(tempFilePath)
        }

    companion object {
        const val AUTHORITY = "com.skyeyes.base.file_provider"
        const val TakePictureCacheKey = "TakePictureCacheKey"
        const val DefaultRequestType = "DefaultRequestType"
        const val PhotoRequestKey = "RequestTypeKey"
    }


    data class PhotoRequest(
        val requestType: String,
        val maxPhotoNumber: Int
    )

    sealed class Status {
        object Fail : Status()
        object StartLoading : Status()
        class Success(val requestType: String, val fileList: List<File>) : Status()
    }


}