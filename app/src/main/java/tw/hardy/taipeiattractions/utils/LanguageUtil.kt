package tw.hardy.taipeiattractions.utils

import android.content.Context
import android.content.res.Configuration
import tw.hardy.base.App
import tw.hardy.base.data.local.SharePreference
import tw.hardy.taipeiattractions.data.local.LanguageData
import tw.hardy.taipeiattractions.data.local.LanguageType
import java.util.Locale

/**
 * 語言工具類
 * 設定APP LOCALE語言 與手機系統脫鉤
 * 主要修改Application 的 [Locale]
 */
object LanguageUtil {

    /**
     * 取得當前語言
     * 預設繁體中文
     */
    fun getCurrentLanguage(): LanguageData =
        SharePreference.fetchObject<LanguageData>(SharePreference.CURRENT_LANGUAGE) ?: let {
            val defaultLanguage = LanguageData(LanguageType.TraditionalChinese)
            SharePreference.storeObject(
                SharePreference.CURRENT_LANGUAGE,
                defaultLanguage
            )
            defaultLanguage
        }

    /**
     * 儲存當前選擇語言
     */
    fun storeCurrentLanguage(languageData: LanguageData) =
        SharePreference.storeObject(SharePreference.CURRENT_LANGUAGE, languageData)

    fun changeLanguage(
        context: Context = App.getApplication(),
        locale: Locale,
        complete: (context: Context) -> Unit = {},
    ) {
        var newContext = context
        val configuration = Configuration(newContext.resources.configuration)
        configuration.setLocale(locale)
        newContext = newContext.createConfigurationContext(configuration)
        complete.invoke(newContext)
    }

    /**
     * 取得語言列表
     * 參數傳入當前語系的文字
     * 利用[LanguageType]迴圈將每個預設顯示的語言名稱對應成當前語言
     */
    fun getLanguageList(
        chineseTW: String,
        chineseCN: String,
        english: String,
        japanese: String,
        korean: String,
        spanish: String,
        indonesian: String,
        thai: String,
        vietnamese: String,
    ): List<LanguageData> {
        val initLanguageData = arrayListOf<LanguageData>()
        enumValues<LanguageType>().forEach {
            it.languageName = when (it) {
                LanguageType.TraditionalChinese -> chineseTW
                LanguageType.SimplifiedChinese -> chineseCN
                LanguageType.English -> english
                LanguageType.Japanese -> japanese
                LanguageType.Korean -> korean
                LanguageType.Spanish -> spanish
                LanguageType.Indonesian -> indonesian
                LanguageType.Thai -> thai
                LanguageType.Vietnamese -> vietnamese
            }
            initLanguageData.add(LanguageData(languageType = it))
        }
        return initLanguageData
    }
}