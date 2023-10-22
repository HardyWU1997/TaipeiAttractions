package tw.hardy.base.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tw.hardy.base.App
import tw.hardy.base.R
import tw.hardy.base.util.*
import tw.hardy.base.view.dialog.LoadingDialog

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected lateinit var binding: VB
    protected lateinit var application: App

    private val loadingDialog by lazy { LoadingDialog(binding.root.context) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getFragmentBinding(inflater, container)
        application = requireActivity().application as App
        getData()
        observer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setAppBarView()
        initViewModel()
    }

    /**
     * ViewBinding
     */
    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * View Event
     */
    abstract fun initView()

    /**
     * 取得當前fragment viewModel
     */
    open fun getViewModel(): BaseViewModel? = null

    /**
     * 進入畫面時要取得的資料
     */
    open fun getData() {

    }

    /**
     * LiveData 觀察者
     */
    abstract fun observer()

    /**
     * 設定Bar
     */
    open fun setAppBarView() {

    }

    /**
     * 是否顯示 loading
     */
    open fun loading(show: Boolean) = loadingDialog.toggle(show)

    private fun initViewModel() {
        viewScope {
            launch {
                getViewModel()?.let { viewModel ->
                    viewModel.baseUiState.collect { baseUiState ->
                        baseUiState.loading?.let {
                            loading(it)
                        }
                        baseUiState.msg?.let {
                            context?.showDialog(it)
                            viewModel.clearMsg()
                        }
                    }
                }
            }
        }

    }

    fun Fragment.viewScope(scope: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scope.invoke(this)
            }
        }
    }

    /**
     * 判斷是否取得定位權限
     */
    @SuppressLint("CheckResult")
    protected fun doWhenPermissionGranted(doOnSuccess: () -> Unit) {
        val context = binding.root.context
        RxPermissions(this)
            .requestEachCombined(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).subscribe { permission ->
                if (!permission.granted) {
                    context.showDialog(resources.getString(R.string.permission_error)) {
                        context.goDetailSetting()
                    }
                    return@subscribe
                }
                doOnSuccess.invoke()
            }
    }
}