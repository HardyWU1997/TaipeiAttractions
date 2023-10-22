package tw.hardy.base.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tw.hardy.base.App.Companion.getApplication

object SharePreference {

    private const val USER_DATA = "USER_DATA"
    const val CURRENT_LANGUAGE = "CURRENT_LANGUAGE"

    private val sharePreferences: SharedPreferences by lazy {
        getApplication().getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
    }
    private val editor: SharedPreferences.Editor by lazy {
        sharePreferences.edit()
    }

    fun storeString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun storeBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun fetchString(key: String): String {
        return sharePreferences.getString(key, "") ?: ""
    }

    fun fetchBoolean(key: String): Boolean {
        return sharePreferences.getBoolean(key, false)
    }

    inline fun <reified T> storeObject(key: String, value: T?) =
        value?.apply {
            storeString(key, Gson().toJson(this))
        } ?: storeString(key, "")

    inline fun <reified T> fetchObject(key: String): T? {
        fetchString(key).apply {
            if (isEmpty()) return null
            return kotlin.runCatching {
                Gson().fromJson(fetchString(key), T::class.java)
            }.getOrNull()
        }
    }

    inline fun <reified T> fetchList(key: String): List<T>? {
        fetchString(key).apply {
            if (isEmpty()) return null
            return kotlin.runCatching {
                Gson().fromJson<List<T>>(
                    this, object : TypeToken<List<T>>() {}.type
                )
            }.getOrNull()
        }
    }
}