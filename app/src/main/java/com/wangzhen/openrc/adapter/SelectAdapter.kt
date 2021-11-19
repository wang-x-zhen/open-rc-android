package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import java.util.*

class SelectAdapter() : RecyclerView.Adapter<SelectAdapterViewHolder>() {
    var data: List<String> = ArrayList()
    private lateinit var clickListener: (pos: Int) -> Unit?
    var selectPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_item, parent, false)

        return SelectAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectAdapterViewHolder, position: Int) {

        holder.name.text = data[position]
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


    fun setDataList(dataList: List<String>, pos: Int = 0) {
        this.data = dataList
        selectPos = pos
        notifyDataSetChanged()
    }

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }
}

class SelectAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
