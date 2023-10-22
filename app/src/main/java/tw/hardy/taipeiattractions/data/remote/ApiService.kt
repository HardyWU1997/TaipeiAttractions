package tw.hardy.taipeiattractions.data.remote

import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import tw.hardy.taipeiattractions.data.remote.response.AttractionResponse

interface ApiService {

    /**
     * 取得台北景點全部資料
     * @param language 語言
     */
    @GET("{lang}/Attractions/All")
    suspend fun getAllAttraction(
        @HeaderMap headers: Map<String, String>,
        @Path("lang") language: String,
    ): AttractionResponse
}