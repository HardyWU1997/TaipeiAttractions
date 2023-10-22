package tw.hardy.base.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import tw.hardy.base.R
import java.util.*

fun Context.showDialog(
    message: String?,
    confirmText: String = resources.getString(R.string.dialog_confirm),
    dialogCallBack: () -> Unit = {}
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(false)
    cancelable(false)
}.show {
    clearPositiveListeners()
    title(text = resources.getString(R.string.dialog_title))
    message(text = message)
    positiveButton(text = confirmText) {
        dialogCallBack.invoke()
    }
}

fun Context.showRetryDialog(
    message: String?,
    cancelCallBack: () -> Unit = {},
    confirmCallBack: () -> Unit = {}
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(false)
    cancelable(false)
}.show {
    clearPositiveListeners()
    title(text = resources.getString(R.string.dialog_title))
    message(text = message)
    negativeButton(text = resources.getString(R.string.dialog_cancel)) {
        cancelCallBack.invoke()
    }
    positiveButton(text = resources.getString(R.string.dialog_retry)) {
        confirmCallBack.invoke()
    }
}

fun Context.showAreYouSureDialog(
    message: String?,
    confirmText: String = resources.getString(R.string.dialog_confirm),
    cancelText: String = resources.getString(R.string.dialog_cancel),
    cancelCallBack: () -> Unit = {},
    confirmCallBack: () -> Unit = {}
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(false)
}.show {
    clearPositiveListeners()
    title(text = resources.getString(R.string.dialog_title))
    message(text = message)
    negativeButton(text = cancelText) {
        cancelCallBack.invoke()
    }
    positiveButton(text = confirmText) {
        confirmCallBack.invoke()
    }
}

@SuppressLint("CheckResult")
fun Context.showListDialog(
    title: String = "請選擇",
    list: List<String>,
    dialogCallBack: (index: Int, text: String) -> Unit = { _, _ -> }
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(true)
    title(text = title)
}.show {
    clearPositiveListeners()
    listItems(items = list) { _, index, text ->
        dialogCallBack.invoke(index, text.toString())
    }
}

fun Context.showMoreActionDialog(
    message: String?,
    positiveText: String = resources.getString(R.string.dialog_confirm),
    negativeText: String = resources.getString(R.string.dialog_cancel),
    moreText: String = resources.getString(R.string.dialog_more),
    cancelCallBack: () -> Unit = {},
    confirmCallBack: () -> Unit = {},
    moreCallBack: () -> Unit = {}
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(false)
}.show {
    clearPositiveListeners()
    title(text = resources.getString(R.string.dialog_title))
    message(text = message)
    negativeButton(text = negativeText) {
        cancelCallBack.invoke()
    }
    positiveButton(text = positiveText) {
        confirmCallBack.invoke()
    }
    neutralButton(text = moreText) {
        moreCallBack.invoke()
    }
}

/**
 * 攔截返回鍵&按鈕點選不dismiss的dialog
 */
fun Context.showCanNotBackDialog(
    message: String?,
    confirmCallBack: () -> Unit
) = MaterialDialog(this).apply {
    cornerRadius(8f)
    cancelOnTouchOutside(false)
    cancelable(false)
}.show {
    clearPositiveListeners()
    noAutoDismiss()
    title(text = resources.getString(R.string.dialog_title))
    message(text = message)
    positiveButton(text = resources.getString(R.string.dialog_confirm)) {
        confirmCallBack.invoke()
    }
}

fun Context.datePickerDialog(
    activity: Activity,
    complete: (time: String) -> Unit
) {
    val c = Calendar.getInstance()
    val datePicker = DatePickerDialog.OnDateSetListener { _, year, m, d ->
        val month = if (m.toString().length < 2) "0${m + 1}" else "${m + 1}" // 只有一位數的話前面補0
        val day = if (d.toString().length < 2) "0$d" else d.toString()
        complete.invoke("$year-$month-$day")
    }
    DatePickerDialog(
        activity,
        datePicker,
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH),
        c.get(Calendar.DAY_OF_MONTH)
    ).show()//日期選擇器
}

fun Context.dateTimePickerDialog(
    activity: Activity,
    complete: (time: String) -> Unit
) {
    val c = Calendar.getInstance()

    val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, min ->
            complete.invoke("$year-${month + 1}-$dayOfMonth $hourOfDay:$min")
        }
        TimePickerDialog(
            activity,
            timePicker,
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true
        ).show()
    }
    DatePickerDialog(
        activity,
        datePicker,
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH),
        c.get(Calendar.DAY_OF_MONTH)
    ).show()//日期選擇器
}

fun Context.showLogoutDialog(confirmCallBack: () -> Unit = {}) =
    MaterialDialog(this).apply {
        cornerRadius(8f)
        cancelOnTouchOutside(false)
    }.show {
        clearPositiveListeners()
        title(text = resources.getString(R.string.dialog_title))
        message(text = resources.getString(R.string.logout_confirm))
        negativeButton(text = resources.getString(R.string.dialog_cancel)) {
            dismiss()
        }
        positiveButton(text = resources.getString(R.string.dialog_confirm)) {
            dismiss()
            confirmCallBack.invoke()
        }
    }

// view
fun ImageView.loadSrc(drawable: Int) {
    this.setImageDrawable(ContextCompat.getDrawable(context, drawable))
}

fun View.setBackground(drawable: Int) {
    this.background = ContextCompat.getDrawable(context, drawable)
}

fun TextView.setTextViewColor(color: Int) {
    this.setTextColor(ContextCompat.getColor(context, color))
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide(invisible: Boolean = false) {
    this.visibility = if (invisible) View.INVISIBLE else View.GONE
}

fun View.isShow() = visibility == View.VISIBLE

/**
 * @param color 背景色
 * @param dimens 圓角弧度
 */
fun View.setCornerBackground(color: String, @DimenRes dimens: Int) {
    // 建立一個 GradientDrawable 物件
    val gradientDrawable = GradientDrawable()
    // 設定形狀為矩形
    gradientDrawable.shape = GradientDrawable.RECTANGLE
    // 設定背景顏色
    gradientDrawable.setColor(Color.parseColor(color))
    // 設定圓角半徑
    val cornerRadius = resources.getDimension(dimens)
    gradientDrawable.cornerRadius = cornerRadius
    // 設定背景為圓角背景
    this.background = gradientDrawable
}

fun View.setRoundBackground(color: String) {
    // 建立一個 GradientDrawable 物件
    val gradientDrawable = GradientDrawable()
    // 設定形狀為矩形
    gradientDrawable.shape = GradientDrawable.OVAL
    // 設定背景顏色
    gradientDrawable.setColor(Color.parseColor(color))
    // 設定背景為圓角背景
    this.background = gradientDrawable
}

fun Context.startGoogleMap(lat: String = "0", lng: String = "0") {
    kotlin.runCatching {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=$lat,$lng")
            ).setPackage("com.google.android.apps.maps")
        )
    }.getOrNull() ?: run { Toast.makeText(this, resources.getString(R.string.google_map_error), Toast.LENGTH_SHORT).show() }
}

fun Context.goDetailSetting() {
    kotlin.runCatching {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:${packageName}")
        startActivity(intent)
    }
}