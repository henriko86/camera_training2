package com.yuruneji.cameratraining2.presentation.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentSettingBinding
import com.yuruneji.cameratraining2.presentation.setting.view.DatePickerFragment
import com.yuruneji.cameratraining2.presentation.setting.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class SettingFragment : Fragment(),
    DatePickerFragment.OnSelectedDateListener,
    TimePickerFragment.OnSelectedTimeListener {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView()")
        _binding = FragmentSettingBinding.inflate(inflater, container, false)


        // val textView: TextView = binding.textDashboard
        // dashboardViewModel.text.observe(viewLifecycleOwner) {
        //     textView.text = it
        // }

        val userName: TextInputEditText = binding.userName
        viewModel.userName.observe(viewLifecycleOwner) {
            userName.text = Editable.Factory.getInstance().newEditable(it)
        }
        userName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Timber.i("userName: $s")
                if (s!!.length > binding.userNameLayout.counterMaxLength) {
                    binding.userNameLayout.error = "ユーザー名は10文字以内で入力してください"
                } else {
                    binding.userNameLayout.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {            }
        })


        // val backBtn = binding.btnBack
        // backBtn.setOnClickListener {
        //     findNavController().navigate(R.id.action_dashboard_to_home)
        // }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_setting_to_home)
        }

        binding.pickDate.setOnClickListener {
            DatePickerFragment().show(childFragmentManager, "datePicker")
        }

        binding.pickTime.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }

        binding.saveBtn.setOnClickListener {

            if (binding.userNameLayout.error == null) {
                viewModel.saveUserName(userName.text.toString())
            } else {
                lifecycleScope.launch {
                    Toast.makeText(
                        requireContext(),
                        "ユーザー名は10文字以内で入力してください",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView()")
        _binding = null
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        binding.dateText.text =
            String.format(Locale.JAPAN, "%04d/%02d/%02d", year, month + 1, dayOfMonth)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        binding.timeText.text = String.format(Locale.JAPAN, "%02d:%02d", hour, minute)
    }

}
