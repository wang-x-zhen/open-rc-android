package com.wangzhen.openrc.dialog

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.wangzhen.openrc.R
import com.wangzhen.openrc.toast
import kotlinx.android.synthetic.main.input_dalog_layout.*
import kotlinx.android.synthetic.main.input_dalog_layout.view.*
import kotlinx.android.synthetic.main.select_list_dalog_layout.view.*


class InputDialog(var name: String? = null, var onSure: (inputValue: String) -> Unit) :
    DialogFragment() {

    lateinit var layout: View


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {



        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //去除默认白色边框
        layout = inflater.inflate(R.layout.input_dalog_layout, container, false)
        name?.let {
            layout.inputEt.setText(it)
        }
        layout.sureBt.setOnClickListener {
            val v = layout.inputEt.text.toString()
            if (v.isNullOrEmpty()) {
                context.toast("不能为空")
                return@setOnClickListener
            }
            dismiss()
            onSure.invoke(v)
        }
        layout.cancelBt.setOnClickListener {
            dismiss()
        }
        return layout
    }

    private fun getScreenSize(): Point {
        val size = Point()
        val activity = requireActivity()
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        display.getSize(size)
        return size
    }

    override fun onStart() {
        val size = getScreenSize()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.gravity = Gravity.CENTER
        params?.height = (size.y * 0.3).toInt()
        params?.width = (size.x * 0.3).toInt()
        dialog?.window?.attributes = params
        super.onStart()
    }

}