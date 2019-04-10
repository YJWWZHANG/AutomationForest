package com.zqb.automationforest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.noober.background.BackgroundLibrary
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.content.Intent
import android.net.Uri
import android.provider.Settings.canDrawOverlays
import android.os.Build
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.blankj.utilcode.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        BackgroundLibrary.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        et_time.setText(SPUtils.getInstance().getString(Constants.REBOOT_TIME, "10"))
        btn_go_alipay.setOnClickListener {
            AliMobileAutoCollectEnergyUtils.startAlipay(this, 0)
        }
        tv_version.append(AppUtils.getAppVersionName())
        btn_infinite_restart.setOnClickListener {
            if (!StringUtils.isSpace(et_time.text.toString())) {
                SPUtils.getInstance().put(Constants.REBOOT_TIME, et_time.text.toString())
                EventBus.getDefault().post(RunEvent())
            } else {
                ToastUtils.showShort("请设置循环启动时间间隔")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 10)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            if (isShouldHideInput(currentFocus, ev)) {
                KeyboardUtils.hideSoftInput(currentFocus)
            }
            et_time.clearFocus()
            SPUtils.getInstance().put(Constants.REBOOT_TIME, et_time.text.toString())
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(view: View?, motionEvent: MotionEvent): Boolean {
        if (view != null && (view is EditText)) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + view.getHeight()
            val right = left
            +view.getWidth()
            return !(motionEvent.x > left && motionEvent.x < right
                    && motionEvent.y > top && motionEvent.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false
    }


}
