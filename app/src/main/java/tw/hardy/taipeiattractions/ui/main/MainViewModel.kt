package tw.hardy.taipeiattractions.ui.main

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import tw.hardy.base.ui.base.BaseViewModel
import tw.hardy.taipeiattractions.data.local.LanguageData
import tw.hardy.taipeiattractions.data.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : BaseViewModel() {

    // 更換語言可從此發送flow
    private val _curLanguageSharedFlow = MutableSharedFlow<LanguageData>()
    val curLanguageSharedFlow = _curLanguageSharedFlow.asSharedFlow()

    /**
     * 取得當前語言
     */
    fun getCurrentLanguage() = mainRepository.getCurrentLanguage()

    /**
     * 更換語言
     */
    fun changeLanguage(languageData: LanguageData) = viewModelScope.launch {
        mainRepository.storeCurrentLanguage(languageData)
        _curLanguageSharedFlow.emit(languageData)
    }

}