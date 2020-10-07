package com.skailab.nikabuy.account


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentAccountDashboardBinding
import com.skailab.nikabuy.factory.AccountDashboardViewModelFactory
import com.skailab.nikabuy.home.HomeFragmentDirections
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.AccountDashboardViewModel
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class AccountDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAccountDashboardBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = AccountDashboardViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(AccountDashboardViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
               else if(binding.viewModel!!.buyerDashboard==null || binding.viewModel!!.buyerDashboard.value!!.buyer==null)
                {
                    binding.viewModel!!.getBuyer(requireContext())
                }
            }
        })
        binding.headerCoverImage.setOnClickListener {
            container!!.findNavController().navigate(R.id.buyerInfoFragment)
        }
        binding.userProfilePhoto.setOnClickListener {
            container!!.findNavController().navigate(R.id.buyerInfoFragment)
        }
        binding.profileLayout.setOnClickListener {
            container!!.findNavController().navigate(R.id.buyerInfoFragment)
        }
        binding.lnRate.setOnClickListener {
            container!!.findNavController().navigate(R.id.buyerInfoFragment)
        }
        binding.btnAddSaleman.setOnClickListener {
            container!!.findNavController().navigate(AccountDashboardFragmentDirections.actionAccountDashboardFragmentToUpdateSaleManFragment())
        }
        binding.lnDeposit.setOnClickListener {
            container!!.findNavController().navigate(R.id.depositFragment)
        }
        binding.lnPaymentPassword.setOnClickListener {
            container!!.findNavController().navigate(R.id.paymentPasswordFragment)
        }
        binding.lnAddress.setOnClickListener {
            container!!.findNavController().navigate(R.id.contactFragment)
        }
        binding.lnBox.setOnClickListener {
            container!!.findNavController().navigate(R.id.boxFragment)
        }
        binding.lnLogout.setOnClickListener{
            MaterialAlertDialogBuilder(context)
                .setTitle(requireContext().resources.getString(R.string.app_name))
                .setMessage(getString(R.string.do_you_want_sign_out))
                .setNeutralButton(getString(R.string.yes)) { dialog, which ->
                    binding.viewModel!!.signOut(container!!)
                }.setPositiveButton(getString(R.string.no)) { _, which ->
                }
                .show()
        }
        binding.lnAccountLink.setOnClickListener{
            container!!.findNavController().navigate(R.id.accountLinkFragment)
        }
        binding.lnLanguage.setOnClickListener {
            container!!.findNavController().navigate(R.id.languageFragment)
        }
        // Inflate the layout for this fragment
        return binding.root
    }


}
