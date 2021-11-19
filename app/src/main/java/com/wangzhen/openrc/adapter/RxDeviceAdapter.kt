package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.model.RxDevice
import java.text.SimpleDateFormat
import java.util.*

class RxDeviceAdapter : RecyclerView.Adapter<RxDeviceAdapterViewHolder>() {
    private var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxDeviceAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rx_device_item, parent, false)
        return RxDeviceAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RxDeviceAdapterViewHolder, position: Int) {
        val device: RxDevice = Data.rxDeviceList[position]
        holder.deviceName.text = device.name
        holder.deviceIp.text = device.ip
        holder.deviceAdc.text = device.adc.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sd: String = sdf.format(Date(java.lang.String.valueOf(device.refreshTimeMs).toLong()))
        holder.deviceTime.text = sd
        holder.itemView.setOnClickListener {
            Data.rxDeviceList[position].isSelect = !Data.rxDeviceList[position].isSelect
            notifyDataSetChanged()
        }
        holder.itemView.isSelected = Data.rxDeviceList[position].isSelect
    }

    override fun getItemCount(): Int {
        return Data.rxDeviceList.size
    }

}

class RxDeviceAdapterViewHolder(var deviceVew: View) : RecyclerView.ViewHolder(deviceVew) {
    var deviceName: TextView = deviceVew.findViewById(R.id.tv_device_name)
    var deviceIp: TextView = deviceVew.findViewById(R.id.tv_device_ip)
    var deviceAdc: TextView = deviceVew.findViewById(R.id.tv_device_adc)
    var deviceTime: TextView = deviceVew.findViewById(R.id.tv_device_time)
}
