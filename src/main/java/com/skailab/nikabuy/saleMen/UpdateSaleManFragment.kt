package com.skailab.nikabuy.saleMen


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentUpdateSaleManBinding
import com.skailab.nikabuy.factory.UpdateSaleManViewModelFactory
import com.skailab.nikabuy.models.SaleMan
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.UpdateSaleManViewModel

/**
 * A simple [Fragment] subclass.
 */
class UpdateSaleManFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUpdateSaleManBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull<FragmentActivity>(value = this.activity).application
        val viewModelFactory = UpdateSaleManViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(UpdateSaleManViewModel::class.java)
        binding.viewModel!!.getSaleMens(requireContext())
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
        })
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
        binding.btnSave.setOnClickListener {
            var selectedSaleManText=binding.filledSaleMan.text.toString()
            var selectedSaleManCode:String=""
            binding.viewModel!!.saleMens.value!!.forEach {
                if(selectedSaleManText==it.code + " - "+it.name){
                    selectedSaleManCode=it.code
                }
            }
            if(selectedSaleManCode==""){
                binding.viewModel!!.showMadal(requireContext(),getString(R.string.please_select_sale_man))
            }
            else{
                binding.viewModel!!.updateSaleMan(requireContext(),selectedSaleManCode,requireActivity())
            }
        }
        return binding.root
    }
}
