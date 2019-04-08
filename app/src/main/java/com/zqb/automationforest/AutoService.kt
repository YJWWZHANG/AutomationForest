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

/**
 *创建时间:2019/4/8 18:18
 */
class AutoService: Service() {

    private var mHandle = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                0 -> {
                    EventBus.getDefault().post(InputEvent("input keyevent 4"))
                    AliMobileAutoCollectEnergyUtils.startAlipay(Utils.getApp(), 0)
                    sendEmptyMessageDelayed(0, 10000)
                }
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
    fun onMessageEventInput(inputEvent: InputEvent) {
        mHandle.sendEmptyMessage(0)
        ShellUtils.execCmd(inputEvent.input, true)
    }
}