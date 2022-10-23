package com.wangzhen.openrc.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.wangzhen.openrc.R
import com.wangzhen.openrc.view.CenterSeekBar
import kotlinx.android.synthetic.main.scale_dalog_layout.view.*


class SeekBarDialog(
    value: Int = 0,
    private val min: Int = 0,
    private val max: Int = 180,
    private val onSelect: (value: Int) -> Unit
) : DialogFragment() {

    lateinit var layout: View
    lateinit var centerSeekBar: CenterSeekBar
    private var mValue: Int = value

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {

        layout = inflater.inflate(R.layout.seekbar_dalog_layout, container, false)
        centerSeekBar = layout.findViewById(R.id.centerSeekBar) as CenterSeekBar
        centerSeekBar.setCenterModeEnable(false)
        centerSeekBar.setMin(min)
        centerSeekBar.setMax(max)
        centerSeekBar.setCenterModeEnable(false)
        centerSeekBar.setOnSeekBarDrawTextListener { progress ->
            mValue = progress
            "$progress"
        }
        layout.sureBt.setOnClickListener {
            dismiss()
            onSelect.invoke(mValue)
        }
        layout.cancelBt.setOnClickListener {
            dismiss()
        }
        centerSeekBar.progress = mValue
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