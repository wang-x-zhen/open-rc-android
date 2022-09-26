package com.wangzhen.openrc

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.*
import com.wangzhen.openrc.utils.SummerTools.runOnIo
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin


/**
 *  @author w小z
 *  @since : 2021/4/23
 */
class RcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Iconify
            .with(FontAwesomeModule())
            .with(EntypoModule())
            .with(TypiconsModule())
            .with(MaterialModule())
            .with(MaterialCommunityModule())
            .with(MeteoconsModule())
            .with(WeathericonsModule())
            .with(SimpleLineIconsModule())
            .with(IoniconsModule());

        runOnIo {
            Data.dbInit(this)
            Data.db.inputSettingDao().getAll()?.takeIf { !it.isNullOrEmpty() }?.let {
                Data.loadSetting(it[0])
            }
        }
        startKoin {
            androidContext(this@RcApp)
            androidLogger()
            modules(appModule)
        }
    }
}