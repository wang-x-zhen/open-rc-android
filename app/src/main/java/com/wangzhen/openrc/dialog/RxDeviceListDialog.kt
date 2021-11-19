package com.wangzhen.openrc.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangzhen.openrc.R
import com.wangzhen.openrc.adapter.RxDeviceAdapter
import com.wangzhen.openrc.model.RxDevice
import kotlinx.android.synthetic.main.rx_device_list_layout.view.*


class RxDeviceListDialog : DialogFragment() {

    lateinit var layout: View

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.rx_device_list_layout, container, false)
        var rxDeviceAdapter = RxDeviceAdapter()
        val arrayList:MutableList<RxDevice> = ArrayList<RxDevice>().apply {
            add(RxDevice().apply { name = "xxx" })
            add(RxDevice().apply { name = "yyy" })
            add(RxDevice().apply { name = "zzz" })
        }

        layout.rxDeviceRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        layout.rxDeviceRecyclerView.adapter = rxDeviceAdapter
        arrayList?.let {
            print("listData$arrayList")
        }
        return layout
    }


    override fun onStart() {
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = params

        super.onStart()
    }

}