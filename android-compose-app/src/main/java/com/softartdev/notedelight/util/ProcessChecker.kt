package com.softartdev.notedelight.util

import android.content.Context
import com.softartdev.notedelight.BuildConfig
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions

val Context.isInLeakCanaryAnalyzerProcess: Boolean
    get() {
        if (!BuildConfig.DEBUG) return false

        val kClass: KClass<out Any> = Class.forName("leakcanary.LeakCanaryProcess").kotlin
        val instance: Any = kClass.objectInstance ?: kClass.java.getDeclaredConstructor().newInstance()

        val memberFunction: KFunction<*>? = kClass.memberFunctions.find { kFunction ->
            kFunction.name == "isInAnalyzerProcess"
        }
        requireNotNull(memberFunction)

        val res: Any? = memberFunction.call(instance, this)
        return res as Boolean
    }
