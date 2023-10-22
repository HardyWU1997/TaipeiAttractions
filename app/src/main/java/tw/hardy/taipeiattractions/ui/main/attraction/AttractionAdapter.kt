package tw.hardy.taipeiattractions.ui.main.attraction

import android.view.LayoutInflater
import android.view.ViewGroup
import tw.hardy.base.ui.base.BaseAdapter
import tw.hardy.base.util.loadFromNet
import tw.hardy.taipeiattractions.R
import tw.hardy.taipeiattractions.data.remote.response.AttractionData
import tw.hardy.taipeiattractions.databinding.RecyclerviewAttractionItemBinding

class AttractionAdapter : BaseAdapter<AttractionData, RecyclerviewAttractionItemBinding>() {

    override fun getViewBinding(
        parent: ViewGroup,
        viewType: Int,
    ) = RecyclerviewAttractionItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )

    override fun bindData(holderBase: BaseViewHolder, position: Int, data: AttractionData) {
        val binding = holderBase.binding
        val context = holderBase.binding.root
        binding.imgAttraction.loadFromNet(data.images?.getOrNull(0)?.src)
        binding.txtAttractionName.text =
            data.name ?: context.resources.getString(R.string.empty_data)
        binding.txtAttractionInfo.text =
            data.introduction ?: context.resources.getString(R.string.empty_data)
    }
}