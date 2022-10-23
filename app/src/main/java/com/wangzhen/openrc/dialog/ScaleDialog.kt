package com.wangzhen.openrc.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.wangzhen.openrc.R
import com.wangzhen.openrc.view.CenterSeekBar
import kotlinx.android.synthetic.main.scale_dalog_layout.view.*


class ScaleDialog(
    val value: Float = 1.0F,
    private val onSelect: (value: Float) -> Unit
) :
    DialogFragment() {

    lateinit var layout: View
    lateinit var progressScale: CenterSeekBar
    private var mValue = value.scale2Progress()

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        layout = inflater.inflate(R.layout.scale_dalog_layout, container, false)
        progressScale = layout.findViewById(R.id.progressScale) as CenterSeekBar
        progressScale.setCenterModeEnable(true)
        progressScale.setOnSeekBarDrawTextListener { progress ->
            mValue = progress
            "%.2f".format(mValue.progress2Scale())
        }
        layout.sureBt.setOnClickListener {
            dismiss()
            onSelect.invoke(mValue.progress2Scale())
        }
        layout.cancelBt.setOnClickListener {
            dismiss()
        }
        progressScale.progress = mValue
        return layout
    }


    override fun onStart() {
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.CENTER
        dialog?.window?.attributes = params
        dialog?.window?.setBackgroundDrawable(null)
        super.onStart()
    }
}

fun Int.progress2Scale(): Float {
    return if (this < 0) {
        1 + this * 0.9F / 100
    } else {
        1 + this * 4 / 100F
    }
}

fun Float.scale2Progress(): Int {
    return if (this < 1) {
        ((this - 1) * 100 / 0.9F).toInt()
    } else {
        ((this - 1) * 100 / 4).toInt()
    }
}

fun Float.format2f(): String {
    return "%.2f".format(this)
}
