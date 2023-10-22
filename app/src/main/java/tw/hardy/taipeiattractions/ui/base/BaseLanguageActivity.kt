package tw.hardy.taipeiattractions.ui.base

import android.content.Context
import androidx.viewbinding.ViewBinding
import tw.hardy.base.ui.base.BaseActivity
import tw.hardy.taipeiattractions.utils.LanguageUtil
import java.util.Locale

/**
 * 多語言用Activity
 */
abstract class BaseLanguageActivity<VB : ViewBinding> : BaseActivity<VB>() {

    override fun attachBaseContext(newBase: Context?) {
        // 在渲染view之前設定好語言
        var context = newBase
        val locale = Locale(
            LanguageUtil.getCurrentLanguage().languageType.languageLocalCode
        )
        Locale.setDefault(locale)
        LanguageUtil.changeLanguage(locale = locale) {
            context = it
        }
        super.attachBaseContext(context)
    }
}