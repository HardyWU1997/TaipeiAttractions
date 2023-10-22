package tw.hardy.base.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tw.hardy.base.App
import tw.hardy.base.R
import tw.hardy.base.data.remote.ResourceState
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        netWorkErrorMsg: String = App.getApplication().resources.getString(R.string.network_error),
        apiCall: suspend () -> T,
    ): ResourceState<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResourceState.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is ConnectException ->
                        ResourceState.Failure(netWorkErrorMsg)

                    is UnknownHostException ->
                        ResourceState.Failure(netWorkErrorMsg)

                    else -> ResourceState.Failure(throwable.toString())
                }
            }
        }
    }

    /**
     * open data API Header
     */
    fun getAPIHeader() = mapOf("accept" to "application/json")
}