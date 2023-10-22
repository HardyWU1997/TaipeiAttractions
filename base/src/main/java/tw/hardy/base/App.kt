package tw.hardy.base

import android.app.Application

open class App : Application() {

    companion object {
        private lateinit var application: Application

        @JvmStatic
        fun getApplication(): Application {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}