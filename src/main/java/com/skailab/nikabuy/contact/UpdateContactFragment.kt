package com.skailab.nikabuy.contact


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.databinding.FragmentUpdateContactBinding
import com.skailab.nikabuy.factory.UpdateContactViewModelFactory
import com.skailab.nikabuy.models.api.RegionApiResult
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.UpdateContactViewModel

/**
 * A simple [Fragment] subclass.
 */
class UpdateContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUpdateContactBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(value = this.activity).application
        val viewModelFactory = UpdateContactViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding.viewModel = ViewModelProvider(this,viewModelFactory).get(UpdateContactViewModel::class.java)
        binding.viewModel!!.setContact(UpdateContactFragmentArgs.fromBundle(requireArguments()).contact)
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
        binding.viewModel!!.region.observe(viewLifecycleOwner,androidx.lifecycle.Observer { region: RegionApiResult? ->
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

                val districts= arrayListOf<String>()
                binding.viewModel!!.region.value!!.districts!!.forEach {
                    if(it.parentId==binding.viewModel!!.contact.value!!.province!!.id!!){
                        districts.add(it.id.toString() + " - "+it.title)
                    }
                }
                val adapterDistrict: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    districts
                )
                binding.filledDistrict.setAdapter(adapterDistrict)
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
        binding.txtContactName.setText(binding.viewModel!!.contact.value!!.title!!.toString())
        binding.txtPhone.setText(binding.viewModel!!.contact.value!!.contactNumber!!.toString())
        binding.txtAddress.setText(binding.viewModel!!.contact.value!!.address!!.toString())
        if(!binding.viewModel!!.contact.value!!.mapUrl.isNullOrEmpty()){
            binding.txtGoogleMap.setText(binding.viewModel!!.contact.value!!.mapUrl.toString())
        }
        if(binding.viewModel!!.contact.value!!.isDefaultContact!!){
            binding.rndSelect.isChecked=true
        }
        binding.filledCountry.setText(binding.viewModel!!.contact.value!!.country!!.id.toString() + "- " + binding.viewModel!!.contact.value!!.country!!.title)
        binding.filledProvince.setText(binding.viewModel!!.contact.value!!.province!!.id.toString() + "- " + binding.viewModel!!.contact.value!!.province!!.title)
        binding.filledDistrict.setText(binding.viewModel!!.contact.value!!.district!!.id.toString() + "- " + binding.viewModel!!.contact.value!!.district!!.title)
        // Inflate the layout for this fragment
        return  binding.root
    }
    fun onSave(binding: FragmentUpdateContactBinding){
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
