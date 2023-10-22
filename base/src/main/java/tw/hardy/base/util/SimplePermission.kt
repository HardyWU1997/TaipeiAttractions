package tw.hardy.base.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions3.RxPermissions

object SimplePermission {

    fun checkSuccess(
        holder: Any,
        vararg permissions: String,
        successResponse: () -> Unit
    ) {
        val context: Context? = when (holder) {
            is Fragment -> holder.context
            is FragmentActivity -> holder
            else -> throw Exception("holder is not FragmentActivity or Fragment.")
        }
        check(holder, *permissions) { success, _ ->
            if (success) {
                successResponse.invoke()
            } else {
                if (context == null) {
                    return@check
                }
                context.showDialog(message = "該功能需要相應權限！", confirmText = "前往設定") {
                    context.goDetailSetting()
                }
            }
        }
    }

    fun check(
        holder: Any,
        vararg permissions: String,
        result: (success: Boolean, message: String?) -> Unit
    ) = when (holder) {
        is Fragment -> checkPermissions(RxPermissions(holder), permissions, result)
        is FragmentActivity -> checkPermissions(RxPermissions(holder), permissions, result)
        else -> throw Exception("holder is not FragmentActivity or Fragment.")
    }


    private fun checkPermissions(
        rxPermissions: RxPermissions,
        permissions: Array<out String>,
        result: (success: Boolean, message: String?) -> Unit
    ) {
        var success = true
        rxPermissions
            .requestEach(*permissions)
            .doOnComplete {
                result.invoke(success, null)
            }
            .onErrorComplete {
                true
            }
            .subscribe {
                if (!it.granted) {
                    success = false
                }
            }
    }
}