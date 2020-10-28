package com.skailab.nikabuy.contact


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.ContactAdapter
import com.skailab.nikabuy.databinding.FragmentContactBinding
import com.skailab.nikabuy.factory.ContactViewModelFactory
import com.skailab.nikabuy.order.OrderFragmentDirections
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.ContactViewModel
import kotlin.requireNotNull as requireNotNull1

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentContactBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = ContactViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(ContactViewModel::class.java)
        val adapter= ContactAdapter(
            ContactAdapter.OnClickListener {
                container!!.findNavController().navigate(ContactFragmentDirections.actionContactFragmentToUpdateContactFragment(it))
            },
            ContactAdapter.OnDeleteClickListener{
                contact, index ->
                MaterialAlertDialogBuilder(context)
                    .setTitle(requireContext().resources.getString(R.string.app_name))
                    .setMessage(getString(R.string.do_want_to_delete_contact))
                    .setNeutralButton(getString(R.string.yes)) { dialog, which ->
                        binding.viewModel!!.delete(
                            requireContext(),
                            binding.rcvProductItems.adapter as ContactAdapter,
                            contact.id!!
                        )
                    }.setPositiveButton(getString(R.string.no)) { dialog, which ->
                    }
                    .show()
            }
        )
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else
                {
                    binding.viewModel!!.getContacts(requireContext(),adapter)
                }
            }
        })
        binding.rcvProductItems.adapter =adapter
        binding.btnNewContact.setOnClickListener {
            container!!.findNavController().navigate(R.id.newContactFragment)
        }
        lifecycleScope.launchWhenResumed {
           binding.viewModel!!.contacts.value!!.clear()
            adapter.notifyDataSetChanged()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
