package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.model.GPIO
import com.wangzhen.openrc.view.UiRes
import java.util.*

class GpioAdapter : RecyclerView.Adapter<GpioAdapterViewHolder>() {
    var data: List<GPIO> = ArrayList()
    private lateinit var clickListener: (pos: Int) -> Unit?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GpioAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.gpio_item, parent, false)
        return GpioAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: GpioAdapterViewHolder, position: Int) {
        val gpio: GPIO = data[position]
        holder.name.text = gpio.name
        holder.name.setOnClickListener {
            this.clickListener.invoke(position)
            notifyDataSetChanged()
        }
        holder.name.setBackgroundColor(UiRes.listUiColor[position])
    }

    override fun getItemCount(): Int {
        return (this.data.size ?: 0).also {
            print("getItemCount $it")
        }
    }


    fun setDataList(dataList: List<GPIO>) {
        this.data = dataList
        notifyDataSetChanged()
    }

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }
}

class GpioAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
