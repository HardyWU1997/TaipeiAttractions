package tw.hardy.taipeiattractions.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tw.hardy.base.ui.base.BaseEquatable

/**
 * 語言種類
 * 如要新增語言，直接在此新增
 * @param languageName 語言中文
 * @param languageCode 語言於api內的代號
 * @param languageLocalCode 語言於app內語系的代碼
 */
enum class LanguageType(
    var languageName: String,
    val languageCode: String,
    val languageLocalCode: String,
) {
    TraditionalChinese("繁體中文", "zh-tw", "zh_TW"),
    SimplifiedChinese("簡體中文", "zh-cn", "zh"),
    English("英文", "en", "en"),
    Japanese("日文", "ja", "ja"),
    Korean("韓文", "ko", "ko"),
    Spanish("西班牙文", "es", "es"),
    Indonesian("印尼文", "id", "id"),
    Thai("泰文", "th", "th"),
    Vietnamese("越南文", "vi", "vi")
}

/**
 * 語言列表資料
 */
@Parcelize
data class LanguageData(
    val languageType: LanguageType,
) : BaseEquatable, Parcelable {
    override fun getItem() = languageType.languageName
}


