package com.skailab.nikabuy.account


import android.content.Intent
import android.os.Bundle
import android.view.*
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

import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.AccountLinkAdapter
import com.skailab.nikabuy.databinding.FragmentAccountLinkBinding
import com.skailab.nikabuy.factory.AccountLinkViewModelFactory
import com.skailab.nikabuy.factory.BuyerViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.AccountLinkViewModel
import org.json.JSONException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AccountLinkFragment : Fragment() {
    private  var callbackManager: CallbackManager?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentAccountLinkBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = AccountLinkViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(AccountLinkViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else if(binding.viewModel!!.users.value==null || binding.viewModel!!.users.value!!.count()==0)
                {
                    binding.viewModel!!.getAccounts(requireContext(),binding.rcvProductItems.adapter as AccountLinkAdapter)
                }
            }
        })
        binding.rcvProductItems.adapter =AccountLinkAdapter()
        binding.btnMobileLink.setOnClickListener {
            container!!.findNavController().navigate(R.id.accountMobileLinkFragment)
        }
        callbackManager = CallbackManager.Factory.create()
        binding.btnFacebookLink.setOnClickListener {
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
                                    binding.viewModel!!.linkFacebookAccount(id,name,requireContext(),binding.rcvProductItems.adapter as AccountLinkAdapter)
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
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}
