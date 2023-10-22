package tw.hardy.base.util

import android.content.Context
import android.os.Environment
import android.util.Base64
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val photoDir = "photo"
const val apkDir = "apk"
const val jpgSuffix = ".jpg"
const val apkSuffix = ".apk"

suspend fun File.compress(context: Context) = Compressor.compress(context, this) {
    default(width = 600, height = 1050)
    destination(this@compress)
}

fun String?.toBase64(): String {
    if (isNullOrEmpty()) {
        return ""
    }
    return kotlin.runCatching {
        Base64.encodeToString(File(this).readBytes(), Base64.DEFAULT)
    }.getOrNull() ?: ""
}

fun createFileInDir(
    fileSuffix: String,
    dirPath: String,
) = runCatching {
    val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    val fileName = "file" + timeStamp + "_" + UUID.randomUUID()
    val dir = File(dirPath)
    if (!dir.exists()) {
        dir.mkdir()
    }
    File(dir, fileName + fileSuffix).apply {
        createNewFile()
    }
}.getOrNull()

/**
 * 創建file
 * type 例:
 * [Environment.DIRECTORY_PICTURES]
 * [Environment.DIRECTORY_DOWNLOADS]
 */
fun Context.createFileInType(
    fileSuffix: String,
    childDirName: String,
    type: String,
) = kotlin.runCatching {
    val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    val fileName = "file" + timeStamp + "_" + UUID.randomUUID()
    val parentFile = File(getExternalFilesDir(type), childDirName)
    if (!parentFile.exists()) {
        parentFile.mkdir()
    }
    File(parentFile, fileName + fileSuffix).apply {
        createNewFile()
    }
}.getOrNull()

/**
 * 創建file
 * type 例:
 * [Environment.DIRECTORY_PICTURES]
 * [Environment.DIRECTORY_DOWNLOADS]
 */
fun Context.createFile(
    type: String,
    fileName: String,
    fileSuffix: String
) = kotlin.runCatching {
    val storageDir: File? = getExternalFilesDir(type)
    File.createTempFile(fileName, fileSuffix, storageDir)
}.getOrNull()

/**
 * 獲得某種文件下的所有路徑
 * type 例:
 * [Environment.DIRECTORY_PICTURES]
 * [Environment.DIRECTORY_DOWNLOADS]
 */
fun Context.getAllFileUrl(type: String): List<String> {
    val allPicList: MutableList<String> = ArrayList()
    val storageDir = this.getExternalFilesDir(type) ?: return allPicList
    val files = storageDir.listFiles()
    if (files == null || files.isEmpty()) return allPicList
    for (file in files) {
        allPicList.add(file.absolutePath)
    }
    return allPicList
}

/**
 * 刪除某種文件下的所有路徑
 * type 例:
 * [Environment.DIRECTORY_PICTURES]
 * [Environment.DIRECTORY_DOWNLOADS]
 */
fun Context.deleteAllFile(type: String) {
    for (path in getAllFileUrl(type)) {
        deleteFileByPath(path)
    }
}

fun getFileListByPath(dirPath: String): List<File>? = File(dirPath).listFiles()?.toList()

/**
 * 根據路徑刪除File
 */
fun deleteFileByPath(path: String): Boolean = runCatching {
    var success = true
    val photoFile = File(path)
    photoFile.listFiles()?.let {
        for (file in it) {
            if (file.isDirectory) {
                deleteFileByPath(file.absolutePath)
            } else {
                if (!file.delete()) {
                    success = false
                }
            }
        }
    }
    return photoFile.delete() && success
}.getOrNull() ?: false


fun Context.getDirPathInType(
    dirName: String,
    type: String,
): String? {
    kotlin.runCatching {
        val file = File(getExternalFilesDir(type), dirName)
        if (file.exists()) {
            return file.absolutePath
        }
        return if (file.mkdir()) {
            file.absolutePath
        } else {
            null
        }
    }.getOrNull() ?: return null
}

fun Context.getDirPath(
    parentPath: String,
    childDirName: String
): String? {
    kotlin.runCatching {
        val file = File(File(parentPath), childDirName)
        if (file.exists()) {
            return file.absolutePath
        }
        return if (file.mkdir()) {
            file.absolutePath
        } else {
            null
        }
    }.getOrNull() ?: return null
}