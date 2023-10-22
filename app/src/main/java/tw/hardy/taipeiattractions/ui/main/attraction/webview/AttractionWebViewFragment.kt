package tw.hardy.taipeiattractions.ui.main.attraction.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import tw.hardy.base.ui.base.BaseFactory
import tw.hardy.base.ui.base.BaseFragment
import tw.hardy.base.util.hide
import tw.hardy.base.util.showDialog
import tw.hardy.taipeiattractions.R
import tw.hardy.taipeiattractions.data.repository.RepositoryLocator
import tw.hardy.taipeiattractions.databinding.FragmentAttractionWebViewBinding
import tw.hardy.taipeiattractions.ui.main.attraction.AttractionViewModel


class AttractionWebViewFragment : BaseFragment<FragmentAttractionWebViewBinding>() {

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
    ) = FragmentAttractionWebViewBinding.inflate(inflater, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true // 縮放至螢幕大小
            settings.useWideViewPort = true // 將圖片調整至適合webView的大小
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    loading(true)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loading(false)
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    super.onReceivedError(view, request, error)
                    context.showDialog(resources.getString(R.string.fragment_attraction_web_view_error) + error.toString())
                }
            }
        }
    }

    override fun setAppBarView() {
        with(binding) {
            appBar.imgRightBtn.hide()
            appBar.imgBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observer() {
        attractionViewModel.selectItemLiveData.observe(viewLifecycleOwner) {
            loading(true)
            binding.appBar.txtTitle.text =
                it.name ?: resources.getString(R.string.fragment_attraction_detail_title)
            it.url?.let { url ->
                binding.txtUrl.text = url
                binding.webView.loadUrl(url)
            } ?: Toast.makeText(
                binding.root.context,
                resources.getString(R.string.empty_data),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}