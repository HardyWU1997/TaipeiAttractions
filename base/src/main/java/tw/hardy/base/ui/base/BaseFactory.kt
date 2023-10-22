package tw.hardy.base.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaseFactory<VM : ViewModel?>(private val listener: () -> VM? = { null }) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return listener() as? T ?: modelClass.newInstance()
    }
}