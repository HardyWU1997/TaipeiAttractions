package tw.hardy.base.util

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.hardy.base.data.remote.BaseService
import tw.hardy.base.data.remote.RetrofitClient
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class DownloadAPK(
    private val context: Context,
    private var onDownloadAPKProgressListener: OnDownloadAPKProgressListener,
    private var onDownloadAPKListener: OnDownloadAPKListener
) {

    private val saveFileName = "APK"

    fun doDownloadAPK(apkURL: String) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    val file = context.createFile(
                        Environment.DIRECTORY_DOWNLOADS,
                        saveFileName,
                        apkSuffix,
                    )
                    val response =
                        RetrofitClient.create(BaseService::class.java).downloadFile(apkURL)
                            .execute()

                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        var inStream: InputStream? = null
                        var outStream: OutputStream? = null
                        try {
                            inStream = body.byteStream()
                            outStream = file?.outputStream()
                            val contentLength = body.contentLength()
                            var currentLength = 0L
                            val buff = ByteArray(1024)
                            var len = inStream.read(buff)
                            var percent = 0
                            while (len != -1) {
                                outStream?.write(buff, 0, len)
                                currentLength += len
                                val percentage = (currentLength * 100 / contentLength).toInt()
                                if (percentage > percent) {
                                    percent = (currentLength / contentLength * 100).toInt()
                                    withContext(Dispatchers.Main) {
                                        onDownloadAPKProgressListener.onProgress(
                                            currentLength,
                                            contentLength,
                                            percentage
                                        )
                                    }
                                }
                                len = inStream.read(buff)
                            }
                            withContext(Dispatchers.Main) {
                                onDownloadAPKListener.onDownloadComplete(file)
                            }
                        } catch (e: Exception) {
                            e.fillInStackTrace()
                            onDownloadAPKListener.onError(e)
                        } finally {
                            inStream?.close()
                            outStream?.close()
                        }
                    } else {
                        onDownloadAPKListener.onResponseFalse(
                            response.errorBody()!!.string()
                        )
                    }
                }.onFailure {
                    onDownloadAPKListener.onError(it)
                }
            }
        }
    }
}

interface OnDownloadAPKProgressListener {
    fun onProgress(bytesDownloaded: Long, totalBytes: Long, percentage: Int)
}

interface OnDownloadAPKListener {
    fun onDownloadComplete(file: File?)
    fun onError(e: Throwable)
    fun onResponseFalse(error: String)
}