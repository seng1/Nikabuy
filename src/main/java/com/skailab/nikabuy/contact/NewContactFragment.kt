package com.skailab.nikabuy.contact


import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentNewContactBinding
import com.skailab.nikabuy.factory.NewContactViewModelFactory
import com.skailab.nikabuy.models.api.RegionApiResult
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.NewContactViewModel


/**
 * A simple [Fragment] subclass.
 */
class NewContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentNewContactBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = NewContactViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(NewContactViewModel::class.java)
        binding.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else
                {
                   binding.viewModel!!.getRegions(requireContext())
                }
            }
        })
        binding.viewModel!!.region.observe(viewLifecycleOwner,androidx.lifecycle.Observer { region:RegionApiResult? ->
            if(region!=null){
                val coutries= arrayListOf<String>()
                region.countries!!.forEach {
                    coutries.add(it.id.toString() + " - "+it.title)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    coutries
                )
                val country=region.countries[0]
                binding.filledCountry.setAdapter(adapter)
                binding.filledCountry.setText(country.id.toString()+" - "+country.title)
                binding.viewModel!!.contact.value!!.country!!.id=country.id
                val provinces= arrayListOf<String>()
                region.provinces!!.forEach {
                    provinces.add(it.id.toString() + " - "+it.title)
                }
                val provinceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    provinces
                )
                binding.filledProvince.setAdapter(provinceAdapter)
            }
        })
        binding.filledProvince.setOnItemClickListener { parent, view, position, id ->
            val provinceId=binding.filledProvince.text.toString().split(' ')[0].trim().toInt()
            binding.viewModel!!.contact.value!!.province!!.id=provinceId
            val districts= arrayListOf<String>()
            binding.viewModel!!.region.value!!.districts!!.forEach {
                if(it.parentId==provinceId){
                    districts.add(it.id.toString() + " - "+it.title)
                }
            }
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                districts
            )
            binding.filledDistrict.setAdapter(adapter)
            binding.filledDistrict.setText("")
            binding.viewModel!!.contact.value!!.district!!.id=0

        }
        binding.filledDistrict.setOnItemClickListener { parent, view, position, id ->
            val text =binding.filledDistrict.text.toString()
            if(text.isEmpty()){
                binding.viewModel!!.contact.value!!.district!!.id=0
            }
            else{
                binding.viewModel!!.contact.value!!.district!!.id=text.split(' ')[0].trim().toInt()
            }
        }
        binding.btnSave.setOnClickListener {
            onSave(binding)
        }
        // Inflate the layout for this fragment
        return  binding.root
    }
    fun onSave(binding: FragmentNewContactBinding){
        if(binding.txtContactName.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.contact_name_require))
            return
        }
        if(binding.txtPhone.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.phone_number_require))
            return
        }
        if(binding.viewModel!!.contact.value!!.country!!.id==null|| binding.viewModel!!.contact.value!!.country!!.id==0){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.country_require))
            return
        }
        if(binding.viewModel!!.contact.value!!.province!!.id==null|| binding.viewModel!!.contact.value!!.province!!.id==0){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.province_require))
            return
        }
        if(binding.viewModel!!.contact.value!!.district!!.id==null|| binding.viewModel!!.contact.value!!.district!!.id==0){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.district_require))
            return
        }
        if(binding.txtAddress.text.isNullOrEmpty()){
            binding.viewModel!!.showMadal(requireContext(),getString(R.string.address_require))
            return
        }
        binding.viewModel!!.contact.value!!.title=binding.txtContactName.text.toString()
        binding.viewModel!!.contact.value!!.contactNumber=binding.txtPhone.text.toString()
        binding.viewModel!!.contact.value!!.address=binding.txtAddress.text.toString()
        binding.viewModel!!.contact.value!!.mapUrl=binding.txtGoogleMap.text.toString()
        binding.viewModel!!.contact.value!!.isDefaultContact=binding.rndSelect.isChecked
        binding.viewModel!!.hideKeyboard(requireContext(),requireView())
        binding.viewModel!!.save(requireContext(),requireActivity())

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

}
