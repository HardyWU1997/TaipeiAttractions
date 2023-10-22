package tw.hardy.taipeiattractions.data.repository

import tw.hardy.base.data.repository.BaseRepository
import tw.hardy.taipeiattractions.data.remote.ApiService

class AttractionRepository(private val apiService: ApiService) : BaseRepository() {

    /**
     * 取得台北全部景點資料
     * @param language 語言
     */
    suspend fun getAllAttraction(language: String, netWorkErrorMsg: String) =
        safeApiCall(netWorkErrorMsg = netWorkErrorMsg) {
            apiService.getAllAttraction(headers = getAPIHeader(), language = language)
        }
}