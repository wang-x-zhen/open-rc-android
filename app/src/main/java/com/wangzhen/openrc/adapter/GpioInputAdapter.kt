package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.model.Input
import com.wangzhen.openrc.view.UiRes

class GpioInputAdapter : RecyclerView.Adapter<GpioInputAdapterViewHolder>() {
    var data = Data.gpioList
    private lateinit var clickListener: (pos: Int) -> Unit?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GpioInputAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.gpio_input_item, parent, false)

        return GpioInputAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: GpioInputAdapterViewHolder, position: Int) {
        val input: Input =
            Data.gpio2InputList[position].let { Data.inputList[it] }
        holder.name.text = input?.name ?: "未设置"
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

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }
}

class GpioInputAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
