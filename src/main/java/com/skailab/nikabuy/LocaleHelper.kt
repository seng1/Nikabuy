package com.skailab.nikabuy

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import android.util.Base64
import android.webkit.WebView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    fun onAttach(context: Context): Context {
        val lang =
            getPersistedData(context, Locale.getDefault().getLanguage())
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().getLanguage())
    }
    fun bindProductDescription(webView: WebView,descrtipion:String){
        var html="<html><head><style>body{text-align:center;width:100%;}</style></head><body>"+descrtipion+"</body></html>"
        val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")
    }
    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)
       /* return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else
            */
         return   updateResourcesLegacy(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String?): Context {


        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration: Configuration = context.getResources().getConfiguration()
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.getResources()
        val configuration: Configuration = resources.getConfiguration()
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        return context
    }
    fun  getScreenWidthInDp() : Int{
        return  Resources.getSystem().displayMetrics.widthPixels
    }
    fun pxToDp(px:Int):Int{
        return (px/Resources.getSystem().displayMetrics.density).toInt()
    }
    fun priceFormat(price: Double?):String {
        return "$"+getThreeDigit(price!!).toString()
    }
    fun getThreeDigit(value:Double):Double{
        val bd: BigDecimal = BigDecimal(value).setScale(3, RoundingMode.HALF_UP)
        return  bd.toDouble()
    }
}