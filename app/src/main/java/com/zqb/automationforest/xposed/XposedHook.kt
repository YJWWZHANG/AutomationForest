package com.zqb.automationforest.xposed

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.zqb.automationforest.AliMobileAutoCollectEnergyUtils
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 *创建时间:2019/4/6 15:38
 */
class XposedHook : IXposedHookLoadPackage {

    private var mIsFirst = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.eg.android.AlipayGphone") {
            var loadClass: Class<*>? = lpparam.classLoader.loadClass("android.util.Base64")
            if (loadClass != null) {
                XposedHelpers.findAndHookMethod(
                    loadClass,
                    "decode",
                    String::class.java,
                    Integer.TYPE,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                            super.afterHookedMethod(param)
                        }
                    })
            }
            loadClass = lpparam.classLoader.loadClass("android.app.Dialog")
            if (loadClass != null) {
                XposedHelpers.findAndHookMethod(loadClass, "show", object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                        super.afterHookedMethod(param)
                        try {
                            throw NullPointerException()
                        } catch (ignored: Exception) {
                        }

                    }
                })
            }
            loadClass = lpparam.classLoader.loadClass("com.alipay.mobile.base.security.CI")
            if (loadClass != null) {
                XposedHelpers.findAndHookMethod(
                    loadClass,
                    "a",
                    loadClass,
                    Activity::class.java,
                    object : XC_MethodReplacement() {
                        override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam): Any? {
                            return null
                        }
                    })
                XposedHelpers.findAndHookMethod(
                    loadClass,
                    "a",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                            super.afterHookedMethod(param)
                            param!!.result = null
                        }
                    })
            }

            XposedHelpers.findAndHookMethod(
                Application::class.java,
                "attach",
                Context::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        if (mIsFirst) return
                        val loader = (param.args[0] as Context).classLoader
                        var clazz: Class<*>? = loader.loadClass("com.alipay.mobile.nebulacore.ui.H5FragmentManager")
                        if (clazz != null) {
                            val h5FragmentClazz = loader.loadClass("com.alipay.mobile.nebulacore.ui.H5Fragment")
                            if (h5FragmentClazz != null) {
                                XposedHelpers.findAndHookMethod(clazz,
                                    "pushFragment",
                                    h5FragmentClazz,
                                    Boolean::class.javaPrimitiveType,
                                    Bundle::class.java,
                                    Boolean::class.javaPrimitiveType,
                                    Boolean::class.javaPrimitiveType,
                                    object : XC_MethodHook() {
                                        @Throws(Throwable::class)
                                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                                            super.afterHookedMethod(param)
                                            Log.i("fragment", "cur fragment: " + param!!.args[0])
                                            AliMobileAutoCollectEnergyUtils.curH5Fragment = param.args[0]
                                        }
                                    })
                            }
                        }

                        clazz = loader.loadClass("com.alipay.mobile.nebulacore.ui.H5Activity")
                        if (clazz != null) {
                            XposedHelpers.findAndHookMethod(clazz, "onResume", object : XC_MethodHook() {
                                @Throws(Throwable::class)
                                override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                                    super.afterHookedMethod(param)
                                    AliMobileAutoCollectEnergyUtils.h5Activity = param!!.thisObject as Activity
                                }
                            })
                        }

                        clazz = loader.loadClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil")
                        if (clazz != null) {
                            mIsFirst = true
                            val h5PageClazz = loader.loadClass("com.alipay.mobile.h5container.api.H5Page")
                            val jsonClazz = loader.loadClass("com.alibaba.fastjson.JSONObject")
                            if (h5PageClazz != null && jsonClazz != null) {
                                XposedHelpers.findAndHookMethod(clazz,
                                    "rpcCall",
                                    String::class.java,
                                    String::class.java,
                                    String::class.java,
                                    Boolean::class.javaPrimitiveType,
                                    jsonClazz,
                                    String::class.java,
                                    Boolean::class.javaPrimitiveType,
                                    h5PageClazz,
                                    Int::class.javaPrimitiveType,
                                    String::class.java,
                                    Boolean::class.javaPrimitiveType,
                                    Int::class.javaPrimitiveType,
                                    object : XC_MethodHook() {
                                        @Throws(Throwable::class)
                                        override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                                            super.beforeHookedMethod(param)
                                        }

                                        @Throws(Throwable::class)
                                        override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?) {
                                            super.afterHookedMethod(param)
                                            val resp = param!!.result
                                            if (resp != null) {
                                                val method = resp.javaClass.getMethod("getResponse")
                                                val response = method.invoke(resp, *arrayOf()) as String

                                                if (AliMobileAutoCollectEnergyUtils.isRankList(response)) {
                                                    AliMobileAutoCollectEnergyUtils.autoGetCanCollectUserIdList(
                                                        loader,
                                                        response
                                                    )
                                                }

                                                // 第一次是自己的能量，比上面的获取用户信息还要早，所有这里需要记录当前自己的userid值
                                                if (AliMobileAutoCollectEnergyUtils.isUserDetail(response)) {
                                                    AliMobileAutoCollectEnergyUtils.autoGetCanCollectBubbleIdList(
                                                        loader,
                                                        response
                                                    )
                                                }
                                            }
                                        }
                                    })
                            }
                        }

                    }
                })
        }
    }

}