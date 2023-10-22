package tw.hardy.base.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tw.hardy.base.App
import tw.hardy.base.util.showDialog
import tw.hardy.base.view.dialog.LoadingDialog
import java.util.Locale


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected lateinit var application: App
    private val loadingDialog by lazy { LoadingDialog(binding.root.context) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        application = App.getApplication() as App
        initView()
        getData()
        observer()
        initViewModel()
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB

    abstract fun initView()

    abstract fun getViewModel(): BaseViewModel

    abstract fun observer()

    abstract fun getData()

    private fun initViewModel() {
        viewScope {
            launch {
                getViewModel().baseUiState.collect { uiState ->
                    uiState.loading?.let {
                        if (it) {
                            loadingDialog.startLoading()
                        } else {
                            loadingDialog.finishLoading()
                        }
                    }
                    uiState.msg?.let {
                        showDialog(it)
                        getViewModel().clearMsg()
                    }
                }
            }
        }
    }

    protected fun viewScope(scope: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scope.invoke(this)
            }
        }
    }

//    protected fun changeLanguage(
//        context: Context,
//        locale: Locale,
//        complete: (context: Context) -> Unit,
//    ) {
//        var newContext = context
//        val displayMetrics = newContext.resources.displayMetrics
//        val config = newContext.resources.configuration
//        config.setLocale(locale)
//        newContext = newContext.createConfigurationContext(config)
//        resources.updateConfiguration(config, displayMetrics)
//        complete.invoke(newContext)
//    }
}