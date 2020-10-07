package com.skailab.nikabuy.order


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.order.OrderSection
import com.skailab.nikabuy.databinding.FragmentOrderBinding
import com.skailab.nikabuy.factory.OrderViewModelFactory
import com.skailab.nikabuy.models.Order
import com.skailab.nikabuy.models.OrderForBindingList
import com.skailab.nikabuy.models.OrderStatus
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.OrderViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class OrderFragment : Fragment() {
    private val sectionedAdapter:SectionedRecyclerViewAdapter=SectionedRecyclerViewAdapter()
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentOrderBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull1(value = this.activity).application
        val viewModelFactory = OrderViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(OrderViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else if(!binding.viewModel!!.isBack){
                    binding.viewModel!!.getOrderStatuses(requireContext())
                    binding.viewModel!!.onGetOrders(requireContext())
                }
                else{
                    binding.viewModel!!.isBack=false
                }
            }
        })
        binding.viewModel!!.orderStatues.observe(viewLifecycleOwner,androidx.lifecycle.Observer { statues:MutableList<OrderStatus>? ->
            if(statues!=null && statues.count()>0){
                if(binding.chipGroup.childCount==0){
                    binding.viewModel!!.orderStatues.value!!.forEach { it ->
                        val chip = Chip(requireContext())
                        chip.text=it.name + "(" +it.total.toString()+")"
                        chip.id=it.id!!
                        val chipDrawable = ChipDrawable.createFromAttributes(
                            requireContext(),
                            null,
                            0,
                            R.style.Widget_MaterialComponents_Chip_Filter
                        )

                        chip.setChipDrawable(chipDrawable)
                        chip.setChipStartPadding(10F)
                        chip.setOnClickListener {
                            sectionedAdapter.removeAllSections()
                            sectionedAdapter.notifyDataSetChanged()
                            binding.viewModel!!.setStatus(it.id,requireContext())
                        }
                        binding.chipGroup.addView(chip)
                    }
                    binding.chipGroup.check(binding.viewModel!!.orderStatues.value!![0].id!!)
                }
            }
        })
        binding.viewModel!!.newRequestedOrders.observe(viewLifecycleOwner,androidx.lifecycle.Observer { orders:MutableList<Order>? ->
            if(orders!=null && orders.count()>0){
                orders.forEach { it ->
                    sectionedAdapter.addSection(OrderSection(OrderForBindingList(it),OrderSection.OnClickListener{
                        binding.viewModel!!.isBack=true
                        container!!.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(it.order!!.id!!))
                    },OrderSection.OnClickListener{
                        binding.viewModel!!.isBack=true
                        container!!.findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(it.order!!.id!!))
                    },OrderSection.OnClickListener{
                        MaterialDialog(requireContext()).show {
                            input(allowEmpty = false,hint = getString(R.string.print_remark),prefill=it.order!!.printLabel) { dialog, text ->
                                it.order!!.printLabel=text.toString()
                                binding.viewModel!!.printRemark(requireContext(),it.order!!.id!!,text.toString())
                            }
                        }
                    },OrderSection.OnClickListener{
                        MaterialAlertDialogBuilder(context)
                            .setTitle(requireContext().resources.getString(R.string.app_name))
                            .setMessage(getString(R.string.do_you_want_cancel_order)+it.order!!.title+ "?")
                            .setNeutralButton(getString(R.string.yes)) { dialog, which ->
                                binding.viewModel!!.cancelOrder(requireContext(),it)
                            }.setPositiveButton(getString(R.string.no)) { _, which ->
                            }
                            .show()
                    },OrderSection.OnClickListener{
                        binding.viewModel!!.reOrder(requireContext(),container!!,it.order!!.id!!)
                    },OrderSection.OnClickListener{
                        val bottomSheet = OrderListPayFragment(it)
                        bottomSheet.show(requireActivity().supportFragmentManager, "pay")
                    }))
                }
                sectionedAdapter.notifyItemRangeInserted(binding.viewModel!!.previouseInsertIndex,orders.count())

            }
        })
        binding.recyclerview.adapter = sectionedAdapter
        binding.scNested.viewTreeObserver.addOnScrollChangedListener {
            val view = binding.scNested.getChildAt(binding.scNested.getChildCount() - 1) as View
            val diff: Int = view.bottom - (binding.scNested.getHeight() + binding.scNested.getScrollY())
            if (diff == 0) {
                if( binding.viewModel!!.isBack){
                    binding.viewModel!!.isBack=false
                }
                else{
                    binding.viewModel!!.onGetMoreOrder()
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
