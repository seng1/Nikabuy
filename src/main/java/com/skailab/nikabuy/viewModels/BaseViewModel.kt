package com.skailab.nikabuy.viewModels

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.engine.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.models.PriceRange
import com.skailab.nikabuy.room.User
import com.skailab.nikabuy.room.UserDao
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URI
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern


open class BaseViewModel(var database: UserDao?): ViewModel() {
    var isUserRequested= MutableLiveData<Boolean?>()
    private  var dialog: AlertDialog? =null;
    var userEntity = MutableLiveData<User?>()
    var viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var audioSearchLanguage:String="km"
    var productProvider:String="Taobao"
    val  taoboaProvider="Taobao"
    val  alibaba1688Provider="Alibaba1688"
    fun showWaiting(context:Context){
       dialog = SpotsDialog.Builder().setContext(context)
           .setMessage(context.getString(R.string.loading))
           .build()

       dialog!!.show()
    }
    fun showWaiting(context:Context,message:String){
        dialog = SpotsDialog.Builder().setContext(context)
            .setMessage(message)
            .build()
        dialog!!.show()
    }
    fun hideWaiting(){
        if(dialog!=null){
            dialog!!.dismiss()
            dialog=null
        }
    }
    fun showMadal(context:Context,title:String,text:String){
       MaterialAlertDialogBuilder(context)
           .setTitle(title)
           .setMessage(text)
           .setNegativeButton("Ok") { dialog, which ->
               // Respond to negative button press
           }
           .show()
    }
    fun showMadal(context:Context,text:String){
        MaterialAlertDialogBuilder(context)
            .setTitle("Nikabuy")
            .setMessage(text)
            .setNegativeButton("Ok") { dialog, which ->
                // Respond to negative button press
            }
            .show()
    }
    fun getCurrentUser() {
        if(database!=null){
            uiScope.launch {
                userEntity.value = getCurrentUser(1)
                if(userEntity.value!=null){
                    audioSearchLanguage=userEntity.value!!.audioSearchLanguage
                    productProvider=userEntity.value!!.productProvider
                }
                isUserRequested.value=true
            }
        }
    }
    private suspend fun getCurrentUser(id:Long): User? {
        return withContext(Dispatchers.IO) {
            var user = database!!.get(id)
            user
        }
    }
    fun updateProductProvider() {
        if(database!=null){
            uiScope.launch {
                withContext(Dispatchers.IO) {
                   database!!.update(productProvider,1)
                }
            }
        }
    }
    fun updateAudioLanguage() {
        if(database!=null){
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database!!.updateAudioLanguage(audioSearchLanguage,1)
                }
            }
        }
    }
    init {
        isUserRequested.value=false
        getCurrentUser()
    }
    fun  convertFileToBaseString(context: Context,filePath:String,isFileDelete:Boolean):String{
        val targetStream: InputStream = FileInputStream(filePath) as InputStream
        val out = ByteArrayOutputStream()
        var read = 0
        val buffer = ByteArray(1024)
        while (read != -1) {
            read = targetStream.read(buffer)
            if (read != -1) out.write(buffer, 0, read)
        }
        out.close()
        var baseString= Base64.getEncoder().encodeToString(out.toByteArray())
        if(isFileDelete){
            File(filePath).delete()
        }
        return  baseString
    }
    fun displayException(context: Context,ex:Exception){
        hideWaiting()
        if(!isNetworkAvailable(context)){
            showMadal(context,context.resources!!.getString(R.string.internet_problem))
            return
        }
        var errorText=ex.toString()
        if(errorText.contains(":")){
            errorText= errorText.split(':')[errorText.split(':').lastIndex]
            showMadal(context,errorText)
            return
        }
        showMadal(context,ex.toString())
    }
    fun getThreeDigit(value:Double):Double{
        val bd: BigDecimal = BigDecimal(value).setScale(3, RoundingMode.HALF_UP)
        return  bd.toDouble()
    }
    fun getPriceFromQuantityPriceRange(ranges:List<PriceRange>,quantity:Int,context: Context):PriceRange{
        var priceRange=ranges[0]
        ranges.forEach {
            if(it.fromQuantity!!<=quantity && it.toQuantity!!>=quantity){
                priceRange=it
            }

        }
        return priceRange

    }
    fun hideKeyboard(context: Context,view:View){
        val im= context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken,0)
    }
      fun  getBase64(thumbnail: Bitmap?):String{
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        var baseString= Base64.getEncoder().encodeToString(bytes.toByteArray())
        return  baseString
    }
    fun  convertToBitmap(context: Context,uri: Uri):Bitmap
    {
        if(Build.VERSION.SDK_INT<28){
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri)
        }
        val source: ImageDecoder.Source = ImageDecoder.createSource(context.getContentResolver(), uri)
        return ImageDecoder.decodeBitmap(source)
    }
    fun isValidEmail(email:String):Boolean{
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun getDefaultLanguage():String{
       var language=Locale.getDefault().language
        if(language.isNullOrEmpty()){
            language="en"
        }
        if(language=="zh"){
            language="zh-CN"
        }
        return language
    }
    fun isTaobao(provider:String):Boolean{
        if(provider=="Taobao"){
            return  true
        }
        return  false
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
    fun  getScreenWidth() : Int{
        return  Resources.getSystem().displayMetrics.widthPixels
    }
    fun getSpaceCount():Int{
        return  2//getScreenWidth()/250
    }
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        if(!imgUrl.isNullOrEmpty()){
            Picasso.get().load(imgUrl).into(imgView)
        }
    }
    fun bindPrice(textView: TextView, price: Double?) {
        if(price==null){
            textView.text=""
            return
        }
        textView.text="$"+getThreeDigit(price!!).toString()
    }
    fun priceFormat(price: Double?):String {
        return "$"+getThreeDigit(price!!).toString()
    }
}