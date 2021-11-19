package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.model.Input
import java.util.*

class InputAdapter : RecyclerView.Adapter<PinMapAdapterViewHolder>() {
    var data: List<Input> = ArrayList()
    private lateinit var clickListener: (pos: Int) -> Unit?
    var selectPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinMapAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.gpio_item, parent, false)

        return PinMapAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PinMapAdapterViewHolder, position: Int) {
        val input: Input = data[position]
        holder.name.text = input.name
        holder.name.setOnClickListener {
            this.clickListener.invoke(position)
            selectPos = position
            notifyDataSetChanged()
        }
        holder.name.isSelected = selectPos == position
    }

    override fun getItemCount(): Int {
        return (this.data.size ?: 0).also {
            print("getItemCount $it")
        }
    }


    fun setDataList(dataList: List<Input>) {
        this.data = dataList
        notifyDataSetChanged()
    }

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }
}

class PinMapAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
