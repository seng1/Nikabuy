package com.skailab.nikabuy.deposit


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener

import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.DepositAdapter
import com.skailab.nikabuy.bottomfragment.BottomImageFragment
import com.skailab.nikabuy.databinding.FragmentDepositBinding
import com.skailab.nikabuy.factory.DepositViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.DepositViewModel

/**
 * A simple [Fragment] subclass.
 */
class DepositFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentDepositBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = DepositViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(DepositViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else if(binding.viewModel!!.deposits.value==null || binding.viewModel!!.deposits.value!!.count()==0)
                {
                    binding.viewModel!!.getDeposits(requireContext())
                }
            }
        })
        val adapter= DepositAdapter(
            DepositAdapter.OnClickListener {
                if(!it.depositAccount!!.receiptImage.isNullOrEmpty()){
                    val bottomSheet = BottomImageFragment(it.depositAccount!!.receiptImage!!,it.referenceNumber!!)
                    bottomSheet.show(requireActivity().supportFragmentManager, "image")
                }
            }
        )
        binding.rcvProductItems.adapter =adapter
        val  scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                binding.viewModel!!.getMoreDeposit(requireContext(),adapter)
            }
        }
        binding.rcvProductItems.addOnScrollListener(scrollListener)
        binding.btnNewDeposit.setOnClickListener {
            container!!.findNavController().navigate(R.id.newDepositFragment)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
