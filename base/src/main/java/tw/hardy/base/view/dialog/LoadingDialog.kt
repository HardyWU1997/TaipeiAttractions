package tw.hardy.base.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import tw.hardy.base.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) {

    /**
     * loadingDialog viewBinding
     */
    private val loadingDialogBinding by lazy {
        DialogLoadingBinding.inflate(LayoutInflater.from(context))
    }

    /**
     * loadingDialog
     */
    private val loadingDialog by lazy {
        Dialog(context).apply {
            setContentView(loadingDialogBinding.root)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    fun startLoading() {
        if (!loadingDialog.isShowing) loadingDialog.show()
    }

    fun finishLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    fun toggle(show: Boolean) {
        if (show) startLoading() else finishLoading()
    }
}