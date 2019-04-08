package com.zqb.automationforest

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.blankj.utilcode.util.ShellUtils
import com.blankj.utilcode.util.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 *创建时间:2019/4/8 18:18
 */
class AutoService: Service() {

    private var mIsRun = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (mIsRun) {
                    ShellUtils.execCmd("input keyevent 4", true)
                    AliMobileAutoCollectEnergyUtils.startAlipay(Utils.getApp(), 0)
                }
            }
        }, 10, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageRunEvent(runEvent: RunEvent) {
        mIsRun = true
    }
}