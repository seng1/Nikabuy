package com.skailab.nikabuy.account


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentLoginBinding
import com.skailab.nikabuy.databinding.FragmentRegisterBinding
import com.skailab.nikabuy.factory.LoginViewModelFactory
import com.skailab.nikabuy.factory.RegisterViewModelFactory
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.LoginViewModel
import com.skailab.nikabuy.viewModels.RegisterViewModel
import org.json.JSONException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {
    private  var callbackManager: CallbackManager?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory = RegisterViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel=  ViewModelProvider(this,viewModelFactory).get(RegisterViewModel::class.java)

        binding.registerButton.setOnClickListener {
            onRegister(binding)
        }
        binding.resentVerifcationCodeButton.setOnClickListener {
            binding.viewModel!!.onSendSms(requireContext())
        }
        binding.confirmVerificationButton.setOnClickListener {
            onCofirmVerification(binding,container!!)
        }
        binding.viewModel!!.getSaleMens(requireContext())
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
                                    LoginManager.getInstance().logOut()
                                    binding.viewModel!!.registerWithFacebook(requireContext(),container!!,id,name)
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
        binding.viewModel!!.saleMens.observe(viewLifecycleOwner,androidx.lifecycle.Observer { saleMens: MutableList<SaleMan>?->
            if(saleMens!=null){
                val sales= arrayListOf<String>()
                sales.add(getString(R.string.please_select_sale_man))
                saleMens.forEach {
                    sales.add(it.code + " - "+it.name)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    sales
                )
                binding.filledSaleMan.setText(sales[0])
                binding.filledSaleMan.setAdapter(adapter)
            }
        })
        // Inflate the layout for this fragment
        return  binding.root;
    }
   fun onRegister(binding:FragmentRegisterBinding){
       if(binding.viewModel!!.register.value!!.name.trim().isEmpty()){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.name_require))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       if(binding.viewModel!!.register.value!!.phone.trim().isEmpty()){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.phone_require))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       if(binding.viewModel!!.register.value!!.password.trim().isEmpty()){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.password_require))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       if(binding.viewModel!!.register.value!!.confirmPassword.trim().isEmpty()){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.confirm_password_require))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       if(binding.viewModel!!.register.value!!.confirmPassword!=binding.viewModel!!.register.value!!.password){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.password_and_confirm_password_not_the_same))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       if(binding.viewModel!!.register.value!!.paymentPassword.isEmpty()){
           binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_require))
           return
       }
       val totalLength=binding.viewModel!!.register.value!!.paymentPassword.length
       if(totalLength!=4){
           binding.viewModel!!.showMadal(requireContext(),getString(R.string.payment_password_must_4_digit))
           return
       }


       var selectedSaleManText=binding.filledSaleMan.text.toString()
       binding.viewModel!!.register.value!!.saleManId=null
       binding.viewModel!!.saleMens.value!!.forEach {
           if(selectedSaleManText==it.code + " - "+it.name){
               binding.viewModel!!.register.value!!.saleManId=it.id
           }
       }
       if(binding.viewModel!!.register.value!!.saleManId==null){
           MaterialAlertDialogBuilder(context)
               .setTitle(resources.getString(R.string.app_name))
               .setMessage(resources.getString(R.string.please_select_sale_man))
               .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
               }.show()
           return
       }
       binding.viewModel!!.hideKeyboard(requireContext(),requireView())
       binding.viewModel!!.onSendSms(requireContext())
    }
    fun onCofirmVerification(binding:FragmentRegisterBinding,container: ViewGroup){
        if(binding.viewModel!!.register.value!!.verificationCode!=binding.viewModel!!.register.value!!.confirmVerificationCode){
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.verification_code_not_correct))
                .setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
                }.show()
            return
        }
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.onRegister(requireContext(),container)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
}
