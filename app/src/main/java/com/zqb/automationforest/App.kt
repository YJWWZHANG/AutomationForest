package com.zqb.automationforest

import android.app.Application
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.Utils

/**
 *创建时间:2019/4/6 15:53
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        ServiceUtils.startService(AutoService::class.java)
    }
}