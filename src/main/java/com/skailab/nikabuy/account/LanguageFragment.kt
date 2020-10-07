package com.skailab.nikabuy.account



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.skailab.nikabuy.LocaleHelper
import com.skailab.nikabuy.databinding.FragmentLanguageBinding
import kotlinx.coroutines.Dispatchers.Main


/**
 * A simple [Fragment] subclass.
 */
class LanguageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val binding = FragmentLanguageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.btnKhmer.setOnClickListener {
            LocaleHelper.setLocale(requireActivity(), "km")
            val i = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            ActivityCompat.finishAfterTransition(requireActivity())
        }
        binding.btnEnglish.setOnClickListener {
            LocaleHelper.setLocale(requireActivity(), "")
            val i = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            ActivityCompat.finishAfterTransition(requireActivity())
        }
        binding.btnChina.setOnClickListener{
            LocaleHelper.setLocale(requireActivity(), "zh")
            val i = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            ActivityCompat.finishAfterTransition(requireActivity())
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}
