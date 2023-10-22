package tw.hardy.base.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * 要使用BaseAdapter的data class 都必須實作 Equatable
 */
interface BaseEquatable {
    override fun equals(other: Any?): Boolean
    fun getItem(): Any
}

abstract class BaseAdapter<DataType : BaseEquatable, VB : ViewBinding> :
    ListAdapter<DataType, BaseAdapter<DataType, VB>.BaseViewHolder>(BaseDiffCallBack()) {

    private var listener: ((VB, DataType) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseViewHolder(getViewBinding(parent, viewType))

    override fun onBindViewHolder(holderBase: BaseViewHolder, position: Int) {
        bindData(holderBase, position, getItem(position))
    }

    abstract fun getViewBinding(parent: ViewGroup, viewType: Int): VB

    abstract fun bindData(holderBase: BaseViewHolder, position: Int, data: DataType)

    inner class BaseViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener?.invoke(binding, getItem(layoutPosition))
            }
        }
    }

    fun setItemClickListener(listener: (VB, DataType) -> Unit) {
        this.listener = listener
    }
}

class BaseDiffCallBack<DataType : BaseEquatable> : DiffUtil.ItemCallback<DataType>() {
    override fun areItemsTheSame(oldItem: DataType, newItem: DataType) =
        oldItem.getItem() == newItem.getItem()

    override fun areContentsTheSame(oldItem: DataType, newItem: DataType) = oldItem == newItem
}