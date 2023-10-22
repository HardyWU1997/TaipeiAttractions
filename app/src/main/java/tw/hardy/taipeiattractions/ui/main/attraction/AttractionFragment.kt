package tw.hardy.taipeiattractions.ui.main.attraction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import tw.hardy.base.ui.base.BaseFactory
import tw.hardy.base.ui.base.BaseFragment
import tw.hardy.base.util.hide
import tw.hardy.base.util.isShow
import tw.hardy.base.util.show
import tw.hardy.base.util.showDialog
import tw.hardy.taipeiattractions.R
import tw.hardy.taipeiattractions.data.local.LanguageData
import tw.hardy.taipeiattractions.data.repository.RepositoryLocator
import tw.hardy.taipeiattractions.databinding.FragmentAttractionsBinding
import tw.hardy.taipeiattractions.ui.main.MainViewModel

class AttractionFragment : BaseFragment<FragmentAttractionsBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel> {
        BaseFactory {
            MainViewModel(RepositoryLocator.getMainRepo())
        }
    }

    private val attractionViewModel by activityViewModels<AttractionViewModel> {
        BaseFactory {
            AttractionViewModel(
                RepositoryLocator.getMainRepo(),
                RepositoryLocator.getAttractionRepo()
            )
        }
    }

    private val attractionAdapter by lazy { AttractionAdapter() }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAttractionsBinding.inflate(inflater, container, false)

    override fun initView() = with(binding) {
        initRecyclerview()
        swipeRefreshAttraction.setOnRefreshListener {
            attractionViewModel.getAllAttraction(
                language = attractionViewModel.getCurrentLanguage(),
                errorTitle = resources.getString(R.string.fragment_attraction_get_attraction_error),
                netWorkErrorMsg = resources.getString(tw.hardy.base.R.string.network_error)
            )
        }
    }

    override fun setAppBarView() {
        with(binding) {
            appBar.txtTitle.text = resources.getString(R.string.fragment_attraction_title)
            appBar.imgBack.hide()
            appBar.imgRightBtn.setOnItemClickListener {
                appBar.imgRightBtn.getChooseData<LanguageData>()?.let {
                    mainViewModel.changeLanguage(it)
                } ?: context?.showDialog(message = resources.getString(R.string.unknown_error))
            }
        }
    }

    private fun initRecyclerview() {
        binding.rvAttraction.apply {
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = attractionAdapter
        }
        attractionAdapter.setItemClickListener { _, attractionData ->
            attractionViewModel.openAttractionDetail(attractionData)
            findNavController().navigate(R.id.action_attractionFragment_to_attractionDetailFragment)
        }
    }

    override fun getViewModel() = attractionViewModel

    override fun loading(show: Boolean) {
        binding.swipeRefreshAttraction.isRefreshing = show
        showShimmer(show)
    }

    override fun getData() {
        attractionViewModel.getLanguageList(
            chineseTW = resources.getString(R.string.language_chinese_tw),
            chineseCN = resources.getString(R.string.language_chinese_ch),
            english = resources.getString(R.string.language_en),
            japanese = resources.getString(R.string.language_ja),
            korean = resources.getString(R.string.language_ko),
            spanish = resources.getString(R.string.language_es),
            indonesian = resources.getString(R.string.language_id),
            thai = resources.getString(R.string.language_th),
            vietnamese = resources.getString(R.string.language_vi)
        )
        attractionViewModel.getAllAttraction(
            language = attractionViewModel.getCurrentLanguage(),
            errorTitle = resources.getString(R.string.fragment_attraction_get_attraction_error),
            netWorkErrorMsg = resources.getString(tw.hardy.base.R.string.network_error)
        )
    }

    override fun observer() {
        viewScope {
            launch {
                attractionViewModel.attractionUiState.collect { attractionUiState ->
                    attractionUiState.languageList?.let {
                        binding.appBar.imgRightBtn.bindListDialog(
                            fragmentManager = requireActivity().supportFragmentManager,
                            clazz = LanguageData::class.java,
                            title = resources.getString(R.string.activity_main_choose_language),
                            dataList = it
                        )
                    }
                    attractionUiState.attractionListData?.let {
                        attractionAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun showShimmer(show: Boolean) {
        if (binding.rvAttraction.isShow() == !show) return
        binding.shimmerRvAttraction.isVisible = show
        if (show) {
            binding.shimmerRvAttraction.startShimmer()
            binding.rvAttraction.hide(true)
        } else {
            binding.rvAttraction.show()
            binding.shimmerRvAttraction.stopShimmer()
        }
    }

    override fun onDestroyView() {
        binding.shimmerRvAttraction.stopShimmer()
        super.onDestroyView()
    }

}