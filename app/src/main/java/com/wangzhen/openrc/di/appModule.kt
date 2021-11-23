package com.wangzhen.openrc.di

import com.wangzhen.openrc.vm.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { SettingViewModel() }
}