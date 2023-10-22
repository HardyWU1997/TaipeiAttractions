package tw.hardy.base.data.remote

sealed class ResourceState<out T> {
    data class Success<out T>(val value: T) : ResourceState<T>()
    data class Failure(
        val errorString: String
    ) : ResourceState<Nothing>()
}