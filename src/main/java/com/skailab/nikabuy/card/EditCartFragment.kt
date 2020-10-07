package com.skailab.nikabuy.card


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Utility
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentEditCartBinding
import com.skailab.nikabuy.factory.EditCartViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.EditCartViewModel
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class EditCartFragment : Fragment() {
    var binding:FragmentEditCartBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditCartBinding.inflate(inflater)
        binding!!.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory = EditCartViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding!!.viewModel = ViewModelProvider(this,viewModelFactory).get(EditCartViewModel::class.java)
        binding!!.viewModel!!.setCart(EditCartFragmentArgs.fromBundle(requireArguments()).cart)
        binding!!.googdImage.setOnClickListener {
            var message= binding!!.viewModel!!.cart.value!!.title
            if(!binding!!.viewModel!!.cart.value!!.skuText.isNullOrEmpty()){
                message+=System.getProperty("line.separator")
                message+=binding!!.viewModel!!.cart.value!!.skuText
            }
            val bottomSheet = BottomImageFragment(binding!!.viewModel!!.cart.value!!.imageUrl!!,message!!)
            bottomSheet.show(requireActivity().supportFragmentManager, "image")
        }
        binding!!.btnUploadImage.setOnClickListener {
            Utility.checkPermission(requireContext())
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 1)
        }
        binding!!.btnRemoveImage.setOnClickListener {
            binding!!.viewModel!!.clearImage()
        }
        binding!!.quantity.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {

        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding!!.quantity.text.isNullOrEmpty()){
                    binding!!.viewModel!!.cart.value!!.quantity=0
                }
                else{
                    binding!!.viewModel!!.cart.value!!.quantity=binding!!.quantity.text.toString().toInt()
                }
            }
        })
        binding!!.messageToCustomer.addTextChangedListener(object:
            TextWatcher {override fun afterTextChanged(s: Editable?) {

        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              binding!!.viewModel!!.cart.value!!.description=binding!!.messageToCustomer.text.toString()
            }
        })
        binding!!.btnSave.setOnClickListener {
            binding!!.viewModel!!.save(requireContext(),container!!)
        }
        // Inflate the layout for this fragment
        return  binding!!.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            onSelectFromGalleryResult(data)
        }
    }
    private fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            try {
                val bm=binding!!.viewModel!!.convertToBitmap(requireContext(), data.data!!)
                binding!!.viewModel!!.setImage(bm)
                binding!!.userImage.setImageBitmap(bm)

            } catch (e: IOException) {
               binding!!.viewModel!!.displayException(requireContext(),e)
            }
        }
    }
}
