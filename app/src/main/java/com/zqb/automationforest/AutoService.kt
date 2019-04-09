package com.zqb.automationforest

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.SystemClock
import com.blankj.utilcode.util.ShellUtils
import com.blankj.utilcode.util.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *创建时间:2019/4/8 18:18
 */
class AutoService: Service() {

    private var mIsRun = false

    private var mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (!mIsRun) {
                mIsRun = true
                SystemClock.sleep(10000)
                ShellUtils.execCmd("input keyevent 4", true)
                AliMobileAutoCollectEnergyUtils.startAlipay(Utils.getApp(), 0)
                mIsRun = false
                sendEmptyMessage(0)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageRunEvent(runEvent: RunEvent) {
        ShellUtils.execCmd("input keyevent 4", true)
        AliMobileAutoCollectEnergyUtils.startAlipay(Utils.getApp(), 0)
        mHandler.sendEmptyMessage(0)
    }
}