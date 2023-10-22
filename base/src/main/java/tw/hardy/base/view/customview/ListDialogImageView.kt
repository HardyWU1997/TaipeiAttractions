package tw.hardy.base.view.customview

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentManager
import tw.hardy.base.ui.base.BaseEquatable
import tw.hardy.base.ui.dialog.ListDialog


class ListDialogImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    init {
        setOnClickListener {
            onEmptyClick?.invoke()
            Toast.makeText(it.context, "無資料！", Toast.LENGTH_SHORT).show()
        }
    }

    private var onEmptyClick: (() -> Unit)? = null
    fun setOnEmptyClickListener(listener: () -> Unit) {
        this.onEmptyClick = listener
    }

    private var onItemClickListener: ((data: Any) -> Unit)? = null
    fun setOnItemClickListener(listener: (data: Any) -> Unit) {
        this.onItemClickListener = listener
    }

    var curData: Any? = null

    fun <DataType> bindListDialog(
        fragmentManager: FragmentManager,
        clazz: Class<DataType>,
        title: String,
        dataList: List<DataType>,
    ) where DataType : BaseEquatable, DataType : Parcelable {
        resetData()
        val listDialog = ListDialog(clazz)
        setOnClickListener {
            listDialog.let { dialog ->
                dialog.showListDialog(
                    fragmentManager = fragmentManager,
                    titleKey = ListDialog.LIST_DIALOG_TITLE,
                    title = title,
                    key = ListDialog.LIST_DIALOG,
                    list = dataList
                )
                dialog.setOnItemClickListener {
                    curData = it
                    onItemClickListener?.invoke(it)
                }
            }

        }
    }

    fun <DataType> getChooseData() = curData as? DataType

    fun resetData() {
        curData = null
    }

    fun clearData(emptyMessage: String) {
        curData = null
        setOnClickListener {
            Toast.makeText(it.context, emptyMessage, Toast.LENGTH_SHORT).show()
        }
    }
}