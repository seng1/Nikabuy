package com.skailab.nikabuy.deposit


import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.skailab.nikabuy.R
import com.skailab.nikabuy.Utility
import com.skailab.nikabuy.databinding.FragmentNewDepositBinding
import com.skailab.nikabuy.factory.NewDepositViewModelFactory
import com.skailab.nikabuy.models.BankAccount
import com.skailab.nikabuy.room.UserDatabase
import com.skailab.nikabuy.viewModels.NewDepositViewModel
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.requireNotNull as requireNotNull1


/**
 * A simple [Fragment] subclass.
 */
class NewDepositFragment : Fragment() {
    private var binding:FragmentNewDepositBinding?=null
    private val REQUEST_CAMERA = 0
    private val SELECT_FILE = 1
    private var userChoosenTask: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentNewDepositBinding.inflate(inflater)
        binding!!.lifecycleOwner = this
        val application = requireNotNull1<FragmentActivity>(value = this.activity).application
        val viewModelFactory = NewDepositViewModelFactory(UserDatabase.getInstance(application).userDao)
        binding!!.viewModel = ViewModelProvider(this,viewModelFactory).get(NewDepositViewModel::class.java)
        binding!!.viewModel!!.isUserRequested.observe(viewLifecycleOwner,androidx.lifecycle.Observer { isUserRequest:Boolean? ->
            if(isUserRequest!!){
                if(binding!!.viewModel!!.userEntity.value==null){
                    container!!.findNavController().navigate(R.id.accountFragment)
                }
                else
                {
                   binding!!.viewModel!!.getBankAccounts(requireContext())
                }
            }
        })
        binding!!.viewModel!!.bankAccounts.observe(viewLifecycleOwner,androidx.lifecycle.Observer { accounts: MutableList<BankAccount> ->
           if(accounts.count()>0 && binding!!.rdBankAccount.childCount==0){
               accounts.forEach {
                   val radioButton = RadioButton(context)
                   radioButton.text = it.bankName
                   radioButton.id = it.id!!
                   binding!!.rdBankAccount.addView(radioButton)
               }
               binding!!.viewModel!!.deposit.value!!.depositAccount!!.bankAccountId=accounts[0].id
               binding!!.rdBankAccount.check(accounts[0].id!!)
           }
        })
        binding!!.lblDate.setOnClickListener {
            showDateDialog(binding!!)
        }
        binding!!.txtDate.setOnClickListener {
            showDateDialog(binding!!)
        }
        binding!!.btnShowDate.setOnClickListener {
            showDateDialog(binding!!)
        }
        binding!!.btnUploadImage.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle(resources.getString(R.string.please_choose))
            val animals =
                arrayOf(resources.getString(R.string.take_photo),resources.getString(R.string.browe_from_galery))
            builder.setItems(animals,
                { dialog, which ->

                    if(which==0){
                        userChoosenTask=resources.getString(R.string.take_photo)
                        cameraIntent()
                    }
                    else if(which==1){
                        userChoosenTask=resources.getString(R.string.browe_from_galery)
                        galleryIntent()
                    }
                })
            val dialog = builder.create()
            dialog.show()
        }
        binding!!.btnConfirmRecharge.setOnClickListener({
            onSubmit(container!!)
        })
        // Inflate the layout for this fragment
        return binding!!.root
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (userChoosenTask == resources.getString(R.string.take_photo))
                    cameraIntent()
                else if (userChoosenTask == resources.getString(R.string.browe_from_galery))
                    galleryIntent()
            }
        }
    }
    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }
    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), SELECT_FILE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode != REQUEST_CAMERA){
                onSelectFromGalleryResult(data)
            }
            else{
                onCaptureImageResult(data!!)
            }
        }
    }
    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        try {
            binding!!.btnUploadImage.setImageBitmap(thumbnail)
            binding!!.viewModel!!.deposit.value!!.depositAccount!!.receiptImage=binding!!.viewModel!!.getBase64(thumbnail)
            binding!!.viewModel!!.deposit.value!!.depositAccount!!.bankAccountId= binding!!.rdBankAccount.checkedRadioButtonId
            binding!!.viewModel!!.uploadImage(binding!!,requireContext())
        } catch (e: IOException) {
            binding!!.viewModel!!.displayException(requireContext(),e)
        }
    }
    private fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            try {
                val bm=binding!!.viewModel!!.convertToBitmap(requireContext(),data.data!!)
                binding!!.btnUploadImage.setImageBitmap(bm)
                binding!!.viewModel!!.deposit.value!!.depositAccount!!.receiptImage=binding!!.viewModel!!.getBase64(bm)
                binding!!.viewModel!!.deposit.value!!.depositAccount!!.bankAccountId= binding!!.rdBankAccount.checkedRadioButtonId
                binding!!.viewModel!!.uploadImage(binding!!,requireContext())
            } catch (e: IOException) {
                binding!!.viewModel!!.displayException(requireContext(),e)
            }
        }
    }
    fun showDateDialog(binding: FragmentNewDepositBinding){
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
              binding.viewModel!!.deposit.value!!.dateText=getDateTimeFormat(year,monthOfYear,dayOfMonth,mHour,mMinute)
              binding.txtDate.text=getDateTimeFormat(year,monthOfYear,dayOfMonth,mHour,mMinute)
              binding.viewModel!!.setHasDate(true)
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    OnTimeSetListener { view, hourOfDay, minute ->
                        binding.viewModel!!.deposit.value!!.dateText=getDateTimeFormat(year,monthOfYear,dayOfMonth,hourOfDay,minute)
                        binding.txtDate.text=getDateTimeFormat(year,monthOfYear,dayOfMonth,hourOfDay,minute)
                    },
                    mHour,
                    mMinute,
                    false
                )
                timePickerDialog.show()

            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
    fun  getDateTimeFormat(year:Int, month:Int,day:Int, hour:Int,minute:Int):String{
        var text =""
        if(day<10) text+="0"+day
        else {
            text+=day.toString()
        }
        text += if(month<10) "/0"+month
        else{
            "/"+month
        }
        text+="/"+year
        text+=" "
        if(hour>11){
            var h=hour-12
            if(h<10){
                text+="0"+h
            }
            else{
                text+=h
            }

        }
        else{
            if(hour<10){
                text+="0"+hour
            }
            else{
                text+=hour
            }
        }
        text+=":"
        if(minute<10){
            text+="0"+ minute
        }
        else{
            text+=minute
        }
        text+=" "
        if(hour>11){
            text+="PM"
        }
        else{
            text+="AM"
        }
        return text

    }

    fun onSubmit(container: ViewGroup){
        if(binding!!.viewModel!!.deposit.value!!.depositAccount!!.receiptImage.isNullOrEmpty()){
            binding!!.viewModel!!.showMadal(requireContext(),getString(R.string.image_require))
            return
        }
        if(binding!!.txtAmount.text.isNullOrEmpty()){
            binding!!.viewModel!!.showMadal(requireContext(),getString(R.string.amount_require))
            return
        }
        binding!!.viewModel!!.deposit.value!!.amount=binding!!.txtAmount.text.toString().toDouble()
        if(binding!!.viewModel!!.deposit.value!!.amount!!<=0){
            binding!!.viewModel!!.showMadal(requireContext(),getString(R.string.amount_require))
            return
        }
        if(binding!!.txtTransaction.text.isNullOrEmpty()){
            binding!!.viewModel!!.showMadal(requireContext(),getString(R.string.transaction_id_require))
            return
        }
        binding!!.viewModel!!.deposit.value!!.depositAccount!!.bankTransactionNumber=binding!!.txtTransaction.text.toString()
        if(binding!!.txtDate.text.isNullOrEmpty()){
            binding!!.viewModel!!.showMadal(requireContext(),getString(R.string.date_require))
            return
        }
        binding!!.viewModel!!.deposit.value!!.dateText=binding!!.txtDate.text.toString()
        binding!!.viewModel!!.deposit.value!!.depositAccount!!.bankAccountId=binding!!.rdBankAccount.checkedRadioButtonId
        binding!!.viewModel!!.onSubmit(requireContext(),container)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }
}
