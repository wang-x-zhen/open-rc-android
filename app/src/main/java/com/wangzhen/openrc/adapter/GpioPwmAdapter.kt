package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.model.Pwm
import com.wangzhen.openrc.view.UiRes

class GpioPwmAdapter : RecyclerView.Adapter<GpioPwmAdapterViewHolder>() {
    var data = Data.gpioList
    private lateinit var clickListener: (pos: Int) -> Unit?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GpioPwmAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.gpio_pwm_channel_item, parent, false)

        return GpioPwmAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: GpioPwmAdapterViewHolder, position: Int) {
        val pwm: Pwm = Data.gpio2PwmList[position].let { Data.pwmList[it] }
        holder.name.text = pwm.name
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

class GpioPwmAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
