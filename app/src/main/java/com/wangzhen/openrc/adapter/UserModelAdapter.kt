package com.wangzhen.openrc.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhen.openrc.R
import com.wangzhen.openrc.data.InputSetting
import java.util.*

class UserModelAdapter : RecyclerView.Adapter<UserModelAdapterViewHolder>() {
    var data: List<InputSetting> = ArrayList()
    private lateinit var clickListener: (pos: Int) -> Unit?
    var selectPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelAdapterViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_model_item, parent, false)

        return UserModelAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserModelAdapterViewHolder, position: Int) {
        val model: InputSetting = data[position]
        holder.userModelName.text = model.name
        holder.userModelName.setOnClickListener {
            this.clickListener.invoke(position)
            selectPos = position
            notifyDataSetChanged()
        }
        holder.userModelName.isSelected = selectPos == position

    }

    override fun getItemCount(): Int {
        return (this.data.size ?: 0).also {
            print("getItemCount $it")
        }
    }


    fun setDataList(dataList: List<InputSetting>) {
        this.data = dataList
        notifyDataSetChanged()
    }

    fun setOnClick(clickListener: (pos: Int) -> Unit?) {
        this.clickListener = clickListener
    }

    fun setPos(pos: Int) {
        this.selectPos = pos
        notifyDataSetChanged()
    }
}

class UserModelAdapterViewHolder(userModelVew: View) : RecyclerView.ViewHolder(userModelVew) {
    var userModelName: TextView = userModelVew.findViewById(R.id.tv_model_name)

}
