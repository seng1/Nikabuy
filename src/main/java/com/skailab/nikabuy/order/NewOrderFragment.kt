package com.skailab.nikabuy.order


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.OrderItemAdapter
import com.skailab.nikabuy.adapter.cart.CartSection
import com.skailab.nikabuy.adapter.orderSubmit.OrderSubmitSection
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentNewOrderBinding
import com.skailab.nikabuy.factory.NewOrderViewModelFactory
import com.skailab.nikabuy.models.Contact
import com.skailab.nikabuy.models.OrderSubmit
import com.skailab.nikabuy.models.ShopCartForBindingList
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.NewOrderViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter


/**
 * A simple [Fragment] subclass.
 */
class NewOrderFragment : Fragment() {
    private val sectionedAdapter: SectionedRecyclerViewAdapter = SectionedRecyclerViewAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =FragmentNewOrderBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory = NewOrderViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(NewOrderViewModel::class.java)
        binding.viewModel!!.setOrder(NewOrderFragmentArgs.fromBundle(requireArguments()).orderSubmit)

        binding.viewModel!!.order.observe(viewLifecycleOwner,androidx.lifecycle.Observer { order:OrderSubmit? ->
           if(order!=null){
               if(binding.rdTranspotationCh.childCount==0){
                   binding.viewModel!!.order.value!!.transpotationChs!!.forEach {
                       val radioButton = RadioButton(context)
                       radioButton.text = it.title
                       radioButton.id = it.id!!
                       binding.rdTranspotationCh.addView(radioButton)
                   }
                   binding.viewModel!!.order.value!!.transportationMethodInChId=binding.viewModel!!.order.value!!.transpotationChs!![0].id
                   binding.rdTranspotationCh.check(binding.viewModel!!.order.value!!.transportationMethodInChId!!)
                   binding.viewModel!!.order.value!!.transpotationKhs!!.forEach {
                       val radioButton = RadioButton(context)
                       radioButton.text = it.title
                       radioButton.setId(it.id!!)
                       binding.rdTranspotationKh.addView(radioButton)
                   }
                   binding.viewModel!!.order.value!!.transportationMethodInKhId=binding.viewModel!!.order.value!!.transpotationKhs!![1].id
                   binding.rdTranspotationKh.check(binding.viewModel!!.order.value!!.transportationMethodInKhId!!)
                   binding.viewModel!!.order.value!!.paymentMethods!!.forEach {
                       val radioButton = RadioButton(context)
                       radioButton.setText(it.title)
                       radioButton.id = it.id!!
                       binding.rdPayment.addView(radioButton)
                   }
                   binding.viewModel!!.order.value!!.paymentMethodId=binding.viewModel!!.order.value!!.paymentMethods!![0].id
                   binding.rdPayment.check(binding.viewModel!!.order.value!!.paymentMethodId!!)
                   binding.viewModel!!.order.value!!.warehouses!!.forEach {
                       val radioButton = RadioButton(context)
                       radioButton.setText(it.title)
                       radioButton.id = it.id!!
                       binding.rdWarehouse.addView(radioButton)
                   }
                   binding.viewModel!!.order.value!!.warehouseId=binding.viewModel!!.order.value!!.warehouses!![0].id
                   binding.rdWarehouse.check(binding.viewModel!!.order.value!!.warehouseId!!)
                   if(binding.viewModel!!.order.value!!.contacts!=null && binding.viewModel!!.order.value!!.contacts!!.count()>0){
                       binding.viewModel!!.order.value!!.contacts!!.forEach {
                           val radioButton = RadioButton(context)
                           radioButton.text = it.displayText
                           radioButton.setId(it.id!!)
                           binding.rdAdress.addView(radioButton)
                       }
                       binding.rdAdress.check(binding.viewModel!!.order.value!!.purchaseOrderContact!!.id!!)
                   }

               }

           }
        })
        binding.rcvProductItems.adapter = sectionedAdapter
        binding.viewModel!!.order.value!!.shopCarts!!.forEach{
            sectionedAdapter.addSection(OrderSubmitSection(it,OrderSubmitSection.OnItemClickListener{
                if(it.skuText==null){
                    it.skuText=""
                }
                val bottomSheet = BottomImageFragment(it.imageUrl!!,it.title!! + "\n" + it.skuText)
                bottomSheet.show(requireActivity().supportFragmentManager, "image")
            }))
        }
        sectionedAdapter.notifyDataSetChanged()
        binding.btnCreateContact.setOnClickListener {
            container!!.findNavController().navigate(R.id.newContactFragment)
        }
        lifecycleScope.launchWhenResumed {
            if(binding.viewModel!=null&&binding.viewModel!!.order.value!=null && (binding.viewModel!!.order.value!!.contacts==null || binding.viewModel!!.order.value!!.contacts!!.count()==0)){
                if(binding.viewModel!!.isUserRequested.value!!){
                    binding.viewModel!!.getContact(requireContext())
                }
            }
        }
        binding.viewModel!!.contacts.observe(viewLifecycleOwner,androidx.lifecycle.Observer { contacts:MutableList<Contact>? ->
          if(contacts!=null && contacts.count()>0){
              var contact:Contact?=null
              contacts.forEach {
                  val radioButton = RadioButton(context)
                  radioButton.text = it.displayText
                  radioButton.setId(it.id!!)
                  binding.rdAdress.addView(radioButton)
                  if(it.isDefaultContact!!){
                      contact=it
                  }
              }
              if(contact!=null){
                  binding.rdAdress.check(contact!!.id!!)
                  binding.viewModel!!.order.value!!.purchaseOrderContact=contact
              }
              else{
                  binding.rdAdress.check(contacts[0].id!!)
                  binding.viewModel!!.order.value!!.purchaseOrderContact=contacts[0]
              }

          }
        })
       binding.btnSubmit.setOnClickListener {
           binding.viewModel!!.order.value!!.transportationMethodInChId=binding.rdTranspotationCh.checkedRadioButtonId
           binding.viewModel!!.order.value!!.transportationMethodInKhId=binding.rdTranspotationKh.checkedRadioButtonId
           binding.viewModel!!.order.value!!.paymentMethodId=binding.rdPayment.checkedRadioButtonId
           binding.viewModel!!.order.value!!.warehouseId=binding.rdWarehouse.checkedRadioButtonId
           binding.viewModel!!.order.value!!.isProtected=binding.chkIsProtected.isChecked
           if(binding.rdAdress.childCount>0){
               var contactId=binding.rdAdress.checkedRadioButtonId
               binding.viewModel!!.order.value!!.contacts!!.forEach {
                   if(it.id==contactId){
                       binding.viewModel!!.order.value!!.purchaseOrderContact=it
                   }
               }
           }
           binding.viewModel!!.onSubmit(requireContext(),container!!)
       }


        // Inflate the layout for this fragment
        return  binding.root
    }
}
