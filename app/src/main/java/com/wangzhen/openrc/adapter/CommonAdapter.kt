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

class CommonAdapter : RecyclerView.Adapter<CommonAdapterViewHolder>() {
    var data: List<String> = ArrayList()
    private lateinit var clickListener: (pos: Int) -> Unit?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.common_item, parent, false)
        return CommonAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommonAdapterViewHolder, position: Int) {
        holder.name.text = data[position]
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


    fun setDataList(dataList: List<String>) {
        this.data = dataList
        notifyDataSetChanged()
    }

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }
}

class CommonAdapterViewHolder(vew: View) : RecyclerView.ViewHolder(vew) {
    var name: TextView = vew.findViewById(R.id.tv_name)

}
