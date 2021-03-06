package com.skailab.nikabuy

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.skailab.nikabuy.LocaleHelper.onAttach


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        res = resources
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(onAttach(base!!, ""))
    }
    companion object {
        var instance: App? = null
            private set
        private var res: Resources? = null

        val resourses: Resources?
            get() = res
    }
}