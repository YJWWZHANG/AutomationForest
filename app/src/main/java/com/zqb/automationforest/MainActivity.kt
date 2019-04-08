package com.zqb.automationforest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.AppUtils
import com.noober.background.BackgroundLibrary
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_go_alipay.setOnClickListener {
            AliMobileAutoCollectEnergyUtils.startAlipay(this, 0)
        }
        tv_version.append(AppUtils.getAppVersionName())
        btn_infinite_restart.setOnClickListener {
            EventBus.getDefault().post(InputEvent("input keyevent 4"))
        }
    }

}
