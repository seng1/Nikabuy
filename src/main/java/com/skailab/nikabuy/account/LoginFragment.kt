package com.skailab.nikabuy.account


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentLoginBinding
import com.skailab.nikabuy.factory.LoginViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.LoginViewModel
import org.json.JSONException
import java.text.DateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    private  var callbackManager:CallbackManager?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = LoginViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(LoginViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value!=null){
                    container!!.findNavController().navigate(R.id.homeFragment)
                }
            }
        })
        binding.btnLogin.setOnClickListener{
            onLoginClick(binding,container!!)
        }
        binding.btnForgetPassword.setOnClickListener {
            container!!.findNavController().navigate(R.id.forgetPasswordFragment)
        }
        callbackManager = CallbackManager.Factory.create()
        binding.btnFacebookLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
            LoginManager.getInstance().loginBehavior=LoginBehavior.WEB_ONLY
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
                                    LoginManager.getInstance().logOut()
                                    binding.viewModel!!.loginWithFacebook(requireContext(),container!!,id,name)
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
        // Inflate the layout for this fragment
        return binding.root
    }
    fun onLoginClick( binding:FragmentLoginBinding,container: ViewGroup){
        if(binding.txtUserName.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),"Phone number is require")
            return
        }
        if(binding.txtPassword.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),"Password is require")
            return
        }
        binding.viewModel!!.userName=binding.txtUserName.text.toString()
        binding.viewModel!!.password=binding.txtPassword.text.toString()
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.login(requireContext(),container)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}
