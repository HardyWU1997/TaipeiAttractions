package tw.hardy.taipeiattractions.ui.main.attraction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tw.hardy.base.data.remote.ResourceState
import tw.hardy.base.ui.base.BaseViewModel
import tw.hardy.taipeiattractions.data.local.LanguageData
import tw.hardy.taipeiattractions.data.remote.response.AttractionData
import tw.hardy.taipeiattractions.data.repository.AttractionRepository
import tw.hardy.taipeiattractions.data.repository.MainRepository

data class AttractionUiState(
    val languageList: List<LanguageData>? = null, // 語言選擇列表資料
    val attractionListData: List<AttractionData>? = null, // 全部台北景點資料
)

class AttractionViewModel(
    private val mainRepository: MainRepository,
    private val attractionRepository: AttractionRepository,
) : BaseViewModel() {

    private val _attractionUiState = MutableStateFlow(AttractionUiState())
    val attractionUiState = _attractionUiState.asStateFlow()

    // 當前選擇的景點
    private val _selectItemLiveData = MutableLiveData<AttractionData>()
    val selectItemLiveData: LiveData<AttractionData> get() = _selectItemLiveData

    /**
     * 取得語言列表
     */
    fun getLanguageList(
        chineseTW: String,
        chineseCN: String,
        english: String,
        japanese: String,
        korean: String,
        spanish: String,
        indonesian: String,
        thai: String,
        vietnamese: String,
    ) = viewModelScope.launch {
        _attractionUiState.update {
            it.copy(
                languageList = mainRepository.getLanguageList(
                    chineseTW = chineseTW,
                    chineseCN = chineseCN,
                    english = english,
                    japanese = japanese,
                    korean = korean,
                    spanish = spanish,
                    indonesian = indonesian,
                    thai = thai,
                    vietnamese = vietnamese
                )
            )
        }
    }

    /**
     * 取得當前語言
     */
    fun getCurrentLanguage() = mainRepository.getCurrentLanguage()

    /**
     * 取得台北景點全部資料
     * @param language 語言
     * @param errorTitle 錯誤訊息開頭
     * @param netWorkErrorMsg 網路錯誤的文字
     */
    fun getAllAttraction(language: LanguageData, errorTitle: String, netWorkErrorMsg: String) =
        viewModelScope.launch {
            emitLoading(true)
            when (val result =
                attractionRepository.getAllAttraction(
                    language = language.languageType.languageCode,
                    netWorkErrorMsg = netWorkErrorMsg
                )) {
                is ResourceState.Success -> {
                    val response = result.value
                    _attractionUiState.update { it.copy(attractionListData = response.dataList) }
                }

                is ResourceState.Failure -> {
                    _attractionUiState.update { it.copy(attractionListData = emptyList()) }
                    emitMsg(errorTitle + result.errorString)
                }
            }
            emitLoading(false)
        }

    /**
     * 選擇項目時呼叫此方法
     */
    fun openAttractionDetail(item: AttractionData) = viewModelScope.launch {
        _selectItemLiveData.value = item
    }
}
