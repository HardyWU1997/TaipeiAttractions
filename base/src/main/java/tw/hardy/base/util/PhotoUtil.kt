package tw.hardy.base.util

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import tw.hardy.base.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 壓縮圖片
 */
fun Bitmap.scaleBitmap(): Bitmap {
    val matrix = Matrix()
    matrix.postScale(0.3f, 0.3f)
    var resizeBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    //在降byte大小
    val outputStream = ByteArrayOutputStream()
    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val bytes = outputStream.toByteArray()
    resizeBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    return resizeBitmap
}

/**
 * 獲得File路徑旋轉了多少
 */
private fun getRotate(path: String?): Int {
    path?.run {
        val exif = ExifInterface(this)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> -90
                ExifInterface.ORIENTATION_ROTATE_180 -> -180
                ExifInterface.ORIENTATION_ROTATE_270 -> -270
                else -> 0
            }
        }
        return 0
    } ?: run {
        return 0
    }
}

/**
 * bitmap to base64
 */
fun bitmapToBase64(bitmap: Bitmap?): String {
    val outputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val profileImage: ByteArray = outputStream.toByteArray()
    return Base64.encodeToString(profileImage, Base64.NO_WRAP)
}


/**
 * base64 to bitmap
 */
fun base64ToBitmap(encodedString: String): Bitmap? {
    val bitmap: Bitmap?
    return try {
        val bitmapArray: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        //opts 壓縮圖片 inSampleSize 越大壓縮比例越大
        val opts = BitmapFactory.Options()
        opts.inSampleSize = 1
        bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size, opts)
        bitmap
    } catch (e: Exception) {
        null
    }

}

/**
 * url to base64
 */
fun String?.urlToBase64(): String {
    if (isNullOrEmpty()) {
        return ""
    }
    return kotlin.runCatching {
        Base64.encodeToString(File(this).readBytes(), Base64.DEFAULT)
    }.getOrNull() ?: ""
}

/**
 * 將bitmap 長寬設置成一樣
 */
fun Bitmap.zoomImg(): Bitmap {
    val fixedWidth = 600F
    val fixedHeight = 1050F
    val newWidth = fixedWidth / width
    val newHeight = fixedHeight / height
    val matrix = Matrix()
    matrix.postScale(newWidth, newHeight)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}


/**
 * bitmap上寫字
 * 浮水印右下角
 */
fun Bitmap.drawText(
    context: Context,
    string: String,
): Bitmap {
    val paint = Paint().apply {
        this.color = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
        this.textSize = context.resources.getDimension(R.dimen.dp_24)
    }
    val textBound = Rect()
    paint.getTextBounds(string, 0, string.length, textBound)
    val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    Canvas(newBitmap).also {
        it.drawBitmap(this, 0f, 0f, null)
        it.drawText(
            string,
            width.toFloat() - textBound.width() - 10F,
            height.toFloat() - textBound.height(),
            paint
        )
        it.save()
        it.restore()
    }
    return newBitmap
}

/**
 * 依據檔案路徑判斷照片是否需要旋轉
 * 再回傳方向正確的bitmap
 */
fun filePathToBitmap(filePath: String?): Bitmap? {
    val oldBitmap = BitmapFactory.decodeFile(filePath)
    val rotate = getRotate(filePath)
    if (rotate != 0) {
        val matrix = Matrix().apply { postRotate(-rotate.toFloat()) }
        return Bitmap.createBitmap(
            oldBitmap,
            0,
            0,
            oldBitmap.width,
            oldBitmap.height,
            matrix,
            true
        )
    }
    return oldBitmap
}

/**
 * 旋轉圖片
 */
private fun Bitmap.rotate(rotate: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(-rotate.toFloat())
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun ImageView.loadFromLocal(fileUrl: String?) {
    if (fileUrl.isNullOrEmpty()) {
        this.setImageBitmap(null)
        return
    }
    load(File(fileUrl.toString())) {
        bitmapConfig(Bitmap.Config.ARGB_8888)
        error(ContextCompat.getDrawable(context, R.drawable.default_photo))
    }
}

fun ImageView.loadFromNet(fileUrl: String?) {
    if (fileUrl.isNullOrEmpty()) return
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(fileUrl)
        .bitmapConfig(Bitmap.Config.ARGB_8888)
        .target(
            onSuccess = {
                this.setImageDrawable(it)
            },
            onError = {
                this.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.default_photo
                    )
                )
            }
        )
        .build()
    imageLoader.enqueue(request)
}