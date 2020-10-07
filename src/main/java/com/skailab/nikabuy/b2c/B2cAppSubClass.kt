package com.skailab.nikabuy.b2c

import android.app.Application
import android.content.res.Configuration
import com.microsoft.identity.client.AuthenticationResult
import com.microsoft.identity.client.PublicClientApplication


class B2cAppSubClass : Application() {
    var authResult: AuthenticationResult? = null
    var publicClient: PublicClientApplication? = null

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        authResult = null
        publicClient = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    companion object {
        var instance: B2cAppSubClass? = null
            private set
    }
}
