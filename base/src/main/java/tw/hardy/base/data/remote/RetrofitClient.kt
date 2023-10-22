package tw.hardy.base.data.remote

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASEURL = "https://www.travel.taipei/open-api/"

    private val retrofitClient: Retrofit.Builder by lazy {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okhttpClient = OkHttpClient().newBuilder()
            .protocols(mutableListOf(Protocol.HTTP_1_1))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient)
    }

    fun <Service> create(service: Class<Service>): Service {
        val apiInterface: Service by lazy {
        retrofitClient.build().create(service)
    }
        return apiInterface
    }
}