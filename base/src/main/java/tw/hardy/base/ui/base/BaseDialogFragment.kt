package tw.hardy.base.ui.base

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tw.hardy.base.R

abstract class BaseDialogFragment<DataType, VB : ViewBinding> : DialogFragment() {
    protected lateinit var binding: VB

    private var listener: ((DataType?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.custom_dialog)
    }

    override fun onStart() {
        super.onStart()
        setWidthPercent(85)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = getDialogBinding(inflater, container)
        dialog?.setCanceledOnTouchOutside(setCanceledOnTouchOutSide())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCancelBtn()?.setOnClickListener {
            dialog?.dismiss()
        }

        getConfirm()?.setOnClickListener {
            listener?.invoke(getData())
        }
    }

    abstract fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun getCancelBtn(): View?

    abstract fun getConfirm(): View?

    abstract fun getData(): DataType?

    open fun setCanceledOnTouchOutSide(): Boolean = false

    open fun setConfirmListener(listener: (DataType?) -> Unit) {
        this.listener = listener
    }

    /**
     * 設定dialog長寬比例
     */
    fun DialogFragment.setPercent(widthPercentage: Int, heightPercentage: Int) {
        val widthPercent = widthPercentage.toFloat() / 100
        val heightPercent = heightPercentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * widthPercent
        val percentHeight = rect.height() * heightPercent
        dialog?.window?.setLayout(percentWidth.toInt(), percentHeight.toInt())
    }

    private fun setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    protected fun DialogFragment.viewScope(scope: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                scope.invoke(this)
            }
        }
    }

    /**
     * 傳一種資料格式用
     */
    open fun <DATA : Parcelable> showDialog(
        fragmentManager: FragmentManager,
        key: String,
        data: DATA?
    ) {
        this.show(fragmentManager, key)
        val bundle = Bundle()
        bundle.putParcelable(key, data)
        this.arguments = bundle
    }

    /**
     * 傳入dialog type
     * 在傳一種資料格式
     */
    open fun <TYPE : Parcelable, DATA : Parcelable> showDialog(
        fragmentManager: FragmentManager,
        key: String,
        type: TYPE?,
        key2: String,
        data: DATA?,
    ) {
        this.show(fragmentManager, key)
        val bundle = Bundle()
        bundle.putParcelable(key, type)
        bundle.putParcelable(key2, data)
        this.arguments = bundle
    }

    /**
     * 列表dialog
     */
    open fun <DATA : Parcelable> showListDialog(
        fragmentManager: FragmentManager,
        titleKey: String,
        title: String,
        key: String,
        list: List<DATA>?
    ) {
        this.show(fragmentManager, key)
        val bundle = Bundle()
        bundle.putString(titleKey, title)
        val arrayList: ArrayList<DATA>? = list?.let { ArrayList(it) }
        bundle.putParcelableArrayList(key, arrayList)
        this.arguments = bundle
    }

    /**
     * 列表 + 搜尋dialog
     */
    open fun <DATA : Parcelable> showSearchListDialog(
        fragmentManager: FragmentManager,
        titleKey: String,
        title: String,
        key: String,
        list: List<DATA>?,
        stringListKey: String,
        stringList: List<String>?
    ) {
        this.show(fragmentManager, key)
        val bundle = Bundle()
        bundle.putString(titleKey, title)
        val arraylist: ArrayList<DATA>? = list?.let { ArrayList(it) }
        bundle.putParcelableArrayList(key, arraylist)
        val edtStringList: ArrayList<String>? = stringList?.let { ArrayList(it) }
        bundle.putStringArrayList(stringListKey, edtStringList)
        this.arguments = bundle
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    fun <T : Parcelable> Bundle.parcelableList(key: String, clazz: Class<T>): ArrayList<T>? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableArrayList(
            key,
            clazz
        )
        else -> @Suppress("DEPRECATION") getParcelableArrayList<T>(key) as? ArrayList<T>
    }
}

/**
 * dialogFragment dismiss
 */
fun Context.dismissDialogFragment(fragmentActivity: FragmentActivity, tag: String) {
    fragmentActivity.supportFragmentManager.findFragmentByTag(tag)?.let {
        val dialogFragment = it as DialogFragment
        if (dialogFragment.showsDialog) {
            dialogFragment.dismiss()
        }
    }
}