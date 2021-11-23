package com.wangzhen.openrc.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.wangzhen.openrc.R
import com.wangzhen.openrc.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRg()
    }

    private fun initRg() {
        binding.viewPager2.adapter = SettingFragmentStateAdapter(this)
        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.radioGroup.check(R.id.rb1)
                    1 -> binding.radioGroup.check(R.id.rb2)
                    2 -> binding.radioGroup.check(R.id.rb3)
                    3 -> binding.radioGroup.check(R.id.rb4)
                }
            }
        })
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb1 -> binding.viewPager2.currentItem = 0
                R.id.rb2 -> binding.viewPager2.currentItem = 1
                R.id.rb3 -> binding.viewPager2.currentItem = 2
                R.id.rb4 -> binding.viewPager2.currentItem = 3
            }
        }
    }
}