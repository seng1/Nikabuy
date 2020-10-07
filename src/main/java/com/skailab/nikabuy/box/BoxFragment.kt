package com.skailab.nikabuy.box


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skailab.nikabuy.EndlessRecyclerViewScrollListener
import com.skailab.nikabuy.R
import com.skailab.nikabuy.adapter.BoxAdapter
import com.skailab.nikabuy.databinding.FragmentBoxBinding
import com.skailab.nikabuy.factory.BoxViewModelFactory
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.BoxViewModel

/**
 * A simple [Fragment] subclass.
 */
class BoxFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentBoxBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = BoxViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding!!.viewModel = ViewModelProvider(this,viewModelFactory).get(BoxViewModel::class.java)
        val adapter= BoxAdapter(
           BoxAdapter.OnClickListener { box, boxItemViewHolder ->
               val bottomSheet = BoxPaymentFragment(box,boxItemViewHolder)
               bottomSheet.show(requireActivity().supportFragmentManager, "Payment")
           }
        )
        binding.rcvProductItems.adapter =adapter
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else{
                   binding.viewModel!!.getBoxes(requireContext(),adapter)
                }
            }
        })
        val  scrollListener: EndlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(binding.rcvProductItems.layoutManager as GridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                binding.viewModel!!.getMoreBox(requireContext(),adapter)
            }
        }
        binding.rcvProductItems.addOnScrollListener(scrollListener)
        binding.chipUnpaid.setOnClickListener {
            binding.viewModel!!.setStatus(requireContext(),adapter,1)
        }
        binding.chipAll.setOnClickListener {
            binding.viewModel!!.setStatus(requireContext(),adapter,0)
        }
        binding.chipPaid.setOnClickListener {
            binding.viewModel!!.setStatus(requireContext(),adapter,2)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

}
