package tw.hardy.base.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

const val dateFullFormat = "yyyy-MM-dd HH:mm:ss"
const val date = "yyyy-MM-dd"
private val dateFormat = SimpleDateFormat(dateFullFormat, Locale.TAIWAN)

fun getNowTimeInMillis() = Calendar.getInstance().timeInMillis

fun getTodayFullTime() =
    DateFormat.format(dateFullFormat, Calendar.getInstance().time)?.toString() ?: ""

fun getToday() = DateFormat.format(date, Calendar.getInstance().time)?.toString() ?: ""

fun getDateToTimestamp(time: String): Long? =
    runCatching { dateFormat.parse(time)?.time }
        .getOrNull()