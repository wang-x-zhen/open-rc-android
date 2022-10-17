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
    private var onUpdateClickListener: ((pos: Int, ip: String) -> Unit)? = null
    private var onSelectedClickListener: ((selected: Boolean, ip: String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RxDeviceAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rx_device_item, parent, false)
        return RxDeviceAdapterViewHolder(view)
    }

    fun setOnUpdateClickListener(onUpdateClickListener: ((pos: Int, ip: String) -> Unit)?) {
        this.onUpdateClickListener = onUpdateClickListener
    }

    fun setOnSelectedClickListener(onSelectedClickListener: ((selected: Boolean, ip: String) -> Unit)?) {
        this.onSelectedClickListener = onSelectedClickListener
    }

    override fun onBindViewHolder(holder: RxDeviceAdapterViewHolder, position: Int) {
        val device: RxDevice = Data.rxDeviceList[position]
        holder.deviceName.text = device.name
        holder.deviceVersion.text = device.version
        holder.deviceIp.text = device.ip
        holder.deviceAdc.text = device.adc.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sd: String = sdf.format(Date(java.lang.String.valueOf(device.refreshTimeMs).toLong()))
        holder.deviceTime.text = sd
        holder.itemView.setOnClickListener {
            Data.rxDeviceList[position].isSelect = !Data.rxDeviceList[position].isSelect
            onSelectedClickListener?.invoke(Data.rxDeviceList[position].isSelect,Data.rxDeviceList[position].ip)
            notifyDataSetChanged()
        }
        holder.firmwareUpdate.setOnClickListener {
            onUpdateClickListener?.invoke(position, device.ip)
        }
        holder.itemView.isSelected = Data.rxDeviceList[position].isSelect
    }

    override fun getItemCount(): Int {
        return Data.rxDeviceList.size
    }

}

class RxDeviceAdapterViewHolder(var deviceVew: View) : RecyclerView.ViewHolder(deviceVew) {
    var deviceName: TextView = deviceVew.findViewById(R.id.tv_device_name)
    var firmwareUpdate: TextView = deviceVew.findViewById(R.id.firmwareUpdate)
    var deviceVersion: TextView = deviceVew.findViewById(R.id.deviceVersion)
    var deviceIp: TextView = deviceVew.findViewById(R.id.tv_device_ip)
    var deviceAdc: TextView = deviceVew.findViewById(R.id.tv_device_adc)
    var deviceTime: TextView = deviceVew.findViewById(R.id.tv_device_time)
}
