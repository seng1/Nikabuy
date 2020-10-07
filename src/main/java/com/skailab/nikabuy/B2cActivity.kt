package com.skailab.nikabuy

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import com.microsoft.identity.client.*
import com.skailab.nikabuy.b2c.B2cAppSubClass
import com.skailab.nikabuy.b2c.B2cConstants
import com.skailab.nikabuy.b2c.B2cHelpers
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.B2CViewModel

class B2cActivity : AppCompatActivity() {
    private var sampleApp: PublicClientApplication? = null
    private var authResult: AuthenticationResult? = null
    private var scopes: Array<String>?=null
    private var state: B2cAppSubClass? = null
    private var  viewModel: B2CViewModel?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b2c)
        val application = requireNotNull(value = this).application
        viewModel= B2CViewModel( UserDatabase.getInstance(application).userDao)
        state = B2cAppSubClass()
        scopes = "email".split("\\s+".toRegex()).toTypedArray()
        sampleApp = state!!.publicClient
        if (sampleApp == null) {
            sampleApp = PublicClientApplication(
                this,
                B2cConstants.CLIENT_ID,
                String.format(B2cConstants.AUTHORITY!!, B2cConstants.TENANT, B2cConstants.SISU_POLICY)
            )
            state!!.publicClient=sampleApp
        }
        onCallApiClicked(scopes!!)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        sampleApp!!.handleInteractiveRequestRedirect(requestCode, resultCode, data)
    }
    private fun onCallApiClicked(scopes: Array<String>) {
        try {
            val currentUser: User? = B2cHelpers.getUserByPolicy(
                sampleApp!!.users,
                B2cConstants.SISU_POLICY
            )
            if (currentUser != null) {
                sampleApp!!.acquireTokenSilentAsync(
                    scopes,
                    currentUser,
                    String.format(
                        B2cConstants.AUTHORITY!!,
                        B2cConstants.TENANT,
                        B2cConstants.SISU_POLICY
                    ),
                    false,
                    authSilentCallback
                )
            } else {
                sampleApp!!.acquireToken(activity, scopes, authInteractiveCallback)
            }
        } catch (e: Exception) {
            viewModel!!.displayException(activity.applicationContext,e)
        }
    }
    val activity: Activity
        get() = this
    private val authSilentCallback: AuthenticationCallback
        private get() = object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: AuthenticationResult) {
                authResult = authenticationResult
                state!!.authResult=authResult
                viewModel!!.login(activity ,authenticationResult.uniqueId)
                sampleApp!!.remove( sampleApp!!.users.get(0))
            }
            override fun onError(exception: MsalException) {
                Toast.makeText(activity.application,exception.toString(),Toast.LENGTH_LONG).show()
            }
            override fun onCancel() {
                val intent = Intent(activity, MainActivity::class.java).apply {
                }
                startActivity(intent)
            }
        }
    private val authInteractiveCallback: AuthenticationCallback
        private get() {
            return object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: AuthenticationResult) {
                    authResult = authenticationResult
                    state!!.authResult = authResult
                    viewModel!!.login(activity ,authenticationResult.uniqueId)
                    sampleApp!!.remove( sampleApp!!.users.get(0))

                }

                override fun onError(exception: MsalException) {
                    Toast.makeText(activity.application,exception.toString(),Toast.LENGTH_LONG).show()
                }

                override fun onCancel() {
                    val intent = Intent(activity, MainActivity::class.java).apply {
                    }
                    startActivity(intent)
                }
            }
        }
}
