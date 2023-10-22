package tw.hardy.base.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BaseUiState(
    val loading: Boolean? = false,
    val msg: String? = null
)

open class BaseViewModel: ViewModel() {

    private val _baseUiState = MutableStateFlow(BaseUiState())
    val baseUiState = _baseUiState.asStateFlow()

    fun emitLoading(loading: Boolean) = viewModelScope.launch {
        _baseUiState.update { it.copy(loading = loading) }
    }

    fun emitMsg(message: String) = viewModelScope.launch {
        _baseUiState.update { it.copy(loading = false, msg = message) }
    }

    fun clearMsg() = viewModelScope.launch {
        _baseUiState.update { it.copy(msg = null) }
    }
}