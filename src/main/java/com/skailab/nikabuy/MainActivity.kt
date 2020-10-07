package com.skailab.nikabuy


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.skailab.nikabuy.LocaleHelper.onAttach
import com.skailab.nikabuy.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var navController:NavController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController!!)
        binding.bottomNavView.setupWithNavController(navController!!)
        NavigationUI.setupWithNavController(binding.bottomNavView, navController!!)
       // settingStatusBarTransparent()
        lifecycleScope.launchWhenResumed {
            navController!!.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.accountFragment,
                    R.id.loginFragment,
                    R.id.registerFragment,
                    R.id.productDetailFragment,
                    R.id.productDescriptionFragment,
                    R.id.productScoreFragment,
                    R.id.addCartFragment,
                    R.id.editCartFragment,
                    R.id.newOrderFragment,
                    R.id.accountDashboardFragment,
                    R.id.buyerInfoFragment,
                    R.id.forgetPasswordFragment,
                    R.id.productTaobaoFragment,
                    R.id.product1688Fragment,
                    R.id.producttaobaoAddCartFragment,
                    R.id.product1688AddCartFragment
                    -> binding.bottomNavView.visibility = View.GONE
                    else -> binding.bottomNavView.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onBackPressed() {
        if(navController!!.currentDestination!=null &&navController!!.currentDestination!!.id==R.id.orderDetailFragment){
            navController!!.navigate(R.id.orderFragment)
        }
        else{
            super.onBackPressed()
        }
    }
    private fun settingStatusBarTransparent() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            val w: Window = window // in Activity's onCreate() for instance
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = Color.BLACK
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
            //setStatusBarTranslucent(true);
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.image->{
                navController!!.navigate(R.id.imageFragment)
            }
            R.id.audio->{
                navController!!.navigate(R.id.audioFragment)
            }
            R.id.textSearch->{
                navController!!.navigate(R.id.textFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(onAttach(base!!))
    }
}

