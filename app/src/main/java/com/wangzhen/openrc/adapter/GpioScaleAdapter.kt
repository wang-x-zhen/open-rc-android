package com.wangzhen.openrc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.dialog.format2f
import com.wangzhen.openrc.view.UiRes

class GpioScaleAdapter : RecyclerView.Adapter<GpioScaleAdapterViewHolder>() {
    var data = Data.inputScaleList
    private var clickListener: ((pos: Int) -> Unit?)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GpioScaleAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.gpio_scale_item, parent, false)

        return GpioScaleAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: GpioScaleAdapterViewHolder, position: Int) {
        val scale: Int = Data.inputScaleList[position]
        holder.name.text = (scale / 100F).format2f()
        holder.name.setOnClickListener {
            this.clickListener?.invoke(position)
        }
        holder.name.setBackgroundColor(UiRes.listUiColor[position])
    }

    override fun getItemCount(): Int {
        return (this.data.size ?: 0).also {
            Log.e("GpioScaleAdapter", "size:$it")
        }
    }

    fun setOnClick(clickListener: ((pos: Int) -> Unit?)?) {
        this.clickListener = clickListener
    }
}

class GpioScaleAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
