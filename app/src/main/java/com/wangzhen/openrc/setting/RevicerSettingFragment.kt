package com.wangzhen.openrc.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangzhen.openrc.R
import com.wangzhen.openrc.adapter.MyItemDivider
import com.wangzhen.openrc.adapter.RxDeviceAdapter
import com.wangzhen.openrc.model.RxDevice
import kotlinx.android.synthetic.main.fragment_revicer_setting.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class RevicerSettingFragment : Fragment() {
    lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_revicer_setting, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDeviceList()
    }

    var adapter = RxDeviceAdapter()
    private fun initDeviceList() {
        rootView.receive_device_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ContextCompat.getDrawable(context!!, R.drawable.custom_divider)!!)
        }
        rootView.receive_device_rv.addItemDecoration(
            MyItemDivider(
                context!!,
                LinearLayoutManager.VERTICAL
            )
        )
        rootView.receive_device_rv.adapter = adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RxDevice) {
        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}