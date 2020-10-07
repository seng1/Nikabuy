package com.skailab.nikabuy.account


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.skailab.nikabuy.B2cActivity
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentAccountBinding
import com.skailab.nikabuy.factory.AccountViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.AccountViewModel
import org.json.JSONException
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {
    private  var callbackManager: CallbackManager?=null
    private  lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = AccountViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel=  ViewModelProvider(this,viewModelFactory).get(AccountViewModel::class.java)
        binding.loginqButton.setOnClickListener {
            container!!.findNavController().navigate(R.id.loginFragment)
        }
        binding.registerButton.setOnClickListener {
            container!!.findNavController().navigate(R.id.registerFragment)
        }
        callbackManager = CallbackManager.Factory.create()
        binding.btnFacebookLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            LoginManager.getInstance().loginBehavior= LoginBehavior.WEB_ONLY
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        val request =
                            GraphRequest.newMeRequest(
                                loginResult!!.accessToken
                            ) { `object`, response ->
                                try {
                                    val name = `object`.getString("name")
                                    val id = `object`.getString("id")
                                    binding.viewModel!!.loginWithFacebook(requireContext(),container!!,id,name)
                                    LoginManager.getInstance().logOut()
                                } catch (ex: JSONException) {
                                    binding.viewModel!!.displayException(requireContext(),ex)
                                }
                            }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name")
                        request.parameters = parameters
                        request.executeAsync()
                    }
                    override fun onCancel() { // App code
                    }
                    override fun onError(exception: FacebookException) { // App code
                        binding.viewModel!!.displayException(requireContext(),exception)
                    }
                })
        }
        binding.btnB2c.setOnClickListener {
            val intent = Intent(requireActivity(), B2cActivity::class.java).apply {
            }
            startActivity(intent)
        }
        binding.btnLanaguage.setOnClickListener{
            container!!.findNavController().navigate(R.id.languageFragment)
        }
        binding.viewModel!!.checkAllAccountLink(requireContext())
        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       super.onActivityResult(requestCode, resultCode, data)
       callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}
