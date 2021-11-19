package com.wangzhen.openrc.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangzhen.openrc.R
import com.wangzhen.openrc.adapter.SelectAdapter
import kotlinx.android.synthetic.main.select_list_dalog_layout.view.*


class SelectListDialog(
    val datas: List<String>,
    val pos: Int = 0,
    val onSelect: (pos: Int) -> Unit
) :
    DialogFragment() {

    lateinit var layout: View

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.select_list_dalog_layout, container, false)
        var selectAdapter = SelectAdapter()

        layout.rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        layout.rv.adapter = selectAdapter
        selectAdapter.setDataList(dataList = datas, pos)
        selectAdapter.setOnClick {
            onSelect(it)
            dismiss()
        }
        return layout
    }


    override fun onStart() {
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.CENTER
        dialog?.window?.attributes = params
        super.onStart()
    }

}