package com.skailab.nikabuy.b2c

object B2cConstants {
    /* Azure AD b2c Configs */
    val AUTHORITY: String? = "https://login.microsoftonline.com/tfp/%s/%s"
    const val TENANT = "nikabuyb2c.onmicrosoft.com"
    const val CLIENT_ID = "d82692f2-c41a-4794-b041-65d737e3394e"
    const val SISU_POLICY = "B2C_1_SkaiMarket_B2C_SIgnInSignUp"
    const val SCOPES = "https://nikabuyb2c.onmicrosoft.com/d82692f2-c41a-4794-b041-65d737e3394e/user.read"
}
