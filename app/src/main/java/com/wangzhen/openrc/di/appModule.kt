package com.wangzhen.openrc.di

import com.wangzhen.openrc.vm.SettingViewModel
import com.wangzhen.openrc.vm.TcpRepo
import com.wangzhen.openrc.vm.TcpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var appModule = module {
    viewModel { SettingViewModel() }
    viewModel { TcpViewModel(get()) }
    single { TcpRepo() }
}