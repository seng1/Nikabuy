package com.skailab.nikabuy.b2c

import android.util.Base64
import com.microsoft.identity.client.User
import java.io.UnsupportedEncodingException

object B2cHelpers {
    fun getUserByPolicy(
        users: List<User>,
        policy: String
    ): User? {
        for (i in users.indices) {
            val curUser = users[i]
            val userIdentifier = Base64UrlDecode(
                curUser.userIdentifier.split("\\.".toRegex()).toTypedArray()[0]
            )
            if (userIdentifier.contains(policy.toLowerCase())) {
                return curUser
            }
        }
        return null as User?
    }

    private fun Base64UrlDecode(s: String): String {
        val data = Base64.decode(
            s,
            Base64.DEFAULT or Base64.URL_SAFE
        )
        var output = ""
        try {
            output = String(data)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } finally {
            return output
        }
    }
}