package tw.hardy.taipeiattractions.data.repository

import tw.hardy.base.data.local.SharePreference
import tw.hardy.base.data.remote.RetrofitClient
import tw.hardy.taipeiattractions.data.remote.ApiService
import tw.hardy.taipeiattractions.utils.LanguageUtil

/**
 * 單例 repository class 用
 */
object RepositoryLocator {

    private val services = mutableMapOf<Class<*>, Any>()

    private fun registerRepository(clazz: Class<*>, service: Any) {
        services[clazz] = service
    }

    private inline fun <reified T> getRepository(): T? {
        return services[T::class.java] as? T
    }

    private val apiService by lazy { RetrofitClient.create(ApiService::class.java) }

    fun getMainRepo(): MainRepository {
        return getRepository<MainRepository>() ?: run {
            val repo = MainRepository(
                apiService = apiService,
                sharePreference = SharePreference,
                languageUtil = LanguageUtil
            )
            registerRepository(MainRepository::class.java, repo)
            repo
        }
    }

    fun getAttractionRepo(): AttractionRepository {
        return getRepository<AttractionRepository>() ?: run {
            val repo = AttractionRepository(
                apiService = apiService
            )
            registerRepository(AttractionRepository::class.java, repo)
            repo
        }
    }
}