package com.softartdev.notedelight.interactor

import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class BiometricActivityHolder {
    private var ref: WeakReference<AppCompatActivity>? = null

    fun attach(activity: AppCompatActivity) {
        ref = WeakReference(activity)
    }

    fun detach() {
        ref?.clear()
        ref = null
    }

    fun current(): AppCompatActivity? = ref?.get()
}
