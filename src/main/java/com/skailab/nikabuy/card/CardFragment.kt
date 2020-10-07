package com.skailab.nikabuy.card


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.CartAdapter
import com.skailab.nikabuy.adapter.cart.CartSection
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentCardBinding
import com.skailab.nikabuy.models.ShopCart
import com.skailab.nikabuy.models.ShopCartForBindingList
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.CartViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

/**
 * A simple [Fragment] subclass.
 */
class CardFragment : Fragment() {
    private val sectionedAdapter: SectionedRecyclerViewAdapter = SectionedRecyclerViewAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentCardBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        binding.viewModel = CartViewModel(UserDatabase.getInstance(application).userDao)
        binding.rcvProductItems.adapter = sectionedAdapter
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else{
                    binding.viewModel!!.getCarts(requireContext())
                }
            }
        })
        binding.viewModel!!.shopCarts.observe(viewLifecycleOwner,androidx.lifecycle.Observer { shops: MutableList<ShopCart> ->
            shops.forEach{
                sectionedAdapter.addSection(it.shopId,CartSection(ShopCartForBindingList(it),CartSection.OnClickListener{
                    it.shop?.carts!!.forEach {cart->
                       cart.isSelected=it.shop?.isSelected
                    }
                  sectionedAdapter.notifyDataSetChanged()
                    binding.viewModel!!.calulateTotal()
                },CartSection.OnItemClickListener{
                    binding.viewModel!!.calulateTotal()
                },CartSection.OnItemClickListener{
                    if(it.skuText==null){
                        it.skuText=""
                    }
                    val bottomSheet = BottomImageFragment(it.imageUrl!!,it.title!! + "\n" + it.skuText)
                    bottomSheet.show(requireActivity().supportFragmentManager, "image")
                },CartSection.OnItemClickListener{
                    container!!.findNavController().navigate(CardFragmentDirections.actionCardFragmentToEditCartFragment(it))
                },CartSection.OnItemClickListener{cart->
                    var message=getString(R.string.do_you_want_to_remove_cart)+ cart.title
                    if(!cart.skuText.isNullOrEmpty()){
                        message+=System.getProperty("line.separator")
                        message+=cart.skuText
                    }
                    message+="?"
                    MaterialAlertDialogBuilder(context)
                        .setTitle(requireContext().resources.getString(R.string.app_name))
                        .setMessage(message)
                        .setNeutralButton(getString(R.string.yes)) { dialog, which ->
                            binding.viewModel!!.delete(requireContext(),cart,it,sectionedAdapter)
                        }.setPositiveButton(R.string.no) { dialog, which ->
                        }
                        .show()
                }))
            }
            sectionedAdapter.notifyDataSetChanged()
        })
        binding.chkSelectAll.setOnClickListener({
            if(binding.chkSelectAll.isChecked){
                binding.chkSelectAll.setText(getString(R.string.un_select_all))
            }
            else{
                binding.chkSelectAll.setText(getString(R.string.select_all))
            }
            binding.viewModel!!.setSelectAll(binding.chkSelectAll.isChecked,sectionedAdapter)
        })
        binding.btSubmit.setOnClickListener({
            binding.viewModel!!.onSubmitCart(requireContext(),container!!)
        })
        // Inflate the layout for this fragment
        return  binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
