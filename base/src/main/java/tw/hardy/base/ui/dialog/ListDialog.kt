package tw.hardy.base.ui.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import tw.hardy.base.databinding.DialogListBinding
import tw.hardy.base.databinding.RecyclerviewBaseItemBinding
import tw.hardy.base.ui.base.BaseAdapter
import tw.hardy.base.ui.base.BaseDialogFragment
import tw.hardy.base.ui.base.BaseEquatable

class ListDialog<DataType>(private val clazz: Class<DataType>) :
    BaseDialogFragment<DataType, DialogListBinding>() where DataType : BaseEquatable, DataType : Parcelable {

    companion object {
        const val LIST_DIALOG = "LIST_DIALOG"
        const val LIST_DIALOG_TITLE = "LIST_DIALOG_TITLE"
    }

    private val listDialogAdapter by lazy { ListDialogAdapter<DataType>() }

    private var chooseData: DataType? = null

    private var itemClickListener: ((DataType) -> Unit)? = null

    override fun getDialogBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DialogListBinding.inflate(inflater, container, false)

    override fun getCancelBtn(): View? = null

    override fun getConfirm(): View? = null

    override fun getData(): DataType? = chooseData

    override fun setCanceledOnTouchOutSide() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.rv.apply {
            adapter = listDialogAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }

        if (arguments != null) {
            arguments?.getString(LIST_DIALOG_TITLE)?.let {
                binding.txtTitle.text = it
            }
            arguments?.parcelableList(LIST_DIALOG, clazz)?.let {
                listDialogAdapter.submitList(it)
            }
        }

        listDialogAdapter.setItemClickListener { _, dataType ->
            dismiss()
            chooseData = dataType
            itemClickListener?.invoke(dataType)
        }
    }

    fun setOnItemClickListener(itemClickListener: ((DataType) -> Unit)) {
        this.itemClickListener = itemClickListener
    }

    fun getChooseData() = chooseData

    fun clearData() {
        chooseData = null
    }

    fun setChooseData(data: DataType?) {
        this.chooseData = data
    }
}

class ListDialogAdapter<DataType : BaseEquatable> :
    BaseAdapter<DataType, RecyclerviewBaseItemBinding>() {

    override fun getViewBinding(parent: ViewGroup, viewType: Int) =
        RecyclerviewBaseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bindData(holderBase: BaseViewHolder, position: Int, data: DataType) {
        val binding = holderBase.binding
        binding.txt.text = data.getItem().toString()
    }
}