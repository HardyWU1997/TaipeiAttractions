package tw.hardy.taipeiattractions.data.repository

import tw.hardy.base.data.local.SharePreference
import tw.hardy.base.data.repository.BaseRepository
import tw.hardy.taipeiattractions.data.local.LanguageData
import tw.hardy.taipeiattractions.data.remote.ApiService
import tw.hardy.taipeiattractions.utils.LanguageUtil

class MainRepository(
    private val apiService: ApiService,
    private val sharePreference: SharePreference,
    private val languageUtil: LanguageUtil,
) : BaseRepository() {

    /**
     * 取得當前語言
     * 預設繁體中文
     */
    fun getCurrentLanguage(): LanguageData =
        languageUtil.getCurrentLanguage()

    /**
     * 儲存當前選擇語言
     */
    fun storeCurrentLanguage(languageData: LanguageData) =
        languageUtil.storeCurrentLanguage(languageData = languageData)

    /**
     * 取得語言列表
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
    ) = languageUtil.getLanguageList(
        chineseTW = chineseTW,
        chineseCN = chineseCN,
        english = english,
        japanese = japanese,
        korean = korean,
        spanish = spanish,
        indonesian = indonesian,
        thai = thai,
        vietnamese = vietnamese
    )
}