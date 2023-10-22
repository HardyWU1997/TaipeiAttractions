package tw.hardy.taipeiattractions.ui.main.attraction.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import tw.hardy.base.ui.base.BaseFactory
import tw.hardy.base.ui.base.BaseFragment
import tw.hardy.base.util.loadFromNet
import tw.hardy.base.util.startGoogleMap
import tw.hardy.taipeiattractions.R
import tw.hardy.taipeiattractions.data.repository.RepositoryLocator
import tw.hardy.taipeiattractions.databinding.FragmentAttractionDetailBinding
import tw.hardy.taipeiattractions.ui.main.attraction.AttractionViewModel


class AttractionDetailFragment : BaseFragment<FragmentAttractionDetailBinding>() {

    private val attractionViewModel by activityViewModels<AttractionViewModel> {
        BaseFactory {
            AttractionViewModel(
                RepositoryLocator.getMainRepo(),
                RepositoryLocator.getAttractionRepo()
            )
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAttractionDetailBinding.inflate(inflater, container, false)

    override fun initView() = with(binding) {
        fab.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                resources.getString(R.string.empty_data),
                Toast.LENGTH_SHORT
            ).show()
        }
        txtUrl.setOnClickListener {
            findNavController().navigate(R.id.action_attractionDetailFragment_to_attractionWebViewFragment)
        }
    }

    override fun setAppBarView() {
        with(binding) {
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun getViewModel() = attractionViewModel

    override fun observer() {
        attractionViewModel.selectItemLiveData.observe(viewLifecycleOwner) { data ->
            with(binding) {
                data.images?.let {
                    bannerView.setData(it) { itemBinding, data ->
                        itemBinding.imageBanner.loadFromNet(data.src)
                    }
                }
                txtTitle.text =
                    data.name ?: resources.getString(R.string.fragment_attraction_detail_title)
                txtName.text = data.name ?: resources.getString(R.string.empty_data)
                txtUrl.text = data.url ?: resources.getString(R.string.empty_data)
                txtDetail.text = data.introduction ?: resources.getString(R.string.empty_data)
                fab.setOnClickListener {
                    context?.startGoogleMap(lng = data.eLong.toString(), lat = data.nLat.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bannerView.setAutoplay(true)
    }

    override fun onPause() {
        super.onPause()
        binding.bannerView.setAutoplay(false)
    }
}