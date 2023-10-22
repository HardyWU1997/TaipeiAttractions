package tw.hardy.base.view.customview

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import tw.hardy.base.ui.base.BaseEquatable
import tw.hardy.base.ui.dialog.ListDialog

class ListDialogTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

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
                    text = it.getItem().toString()
                }
            }

        }
    }

    fun getChooseData() = curData

    fun resetData() {
        curData = null
        text = ""
    }

    fun clearData(emptyMessage: String) {
        curData = null
        text = ""
        setOnClickListener {
            Toast.makeText(it.context, emptyMessage, Toast.LENGTH_SHORT).show()
        }
    }
}