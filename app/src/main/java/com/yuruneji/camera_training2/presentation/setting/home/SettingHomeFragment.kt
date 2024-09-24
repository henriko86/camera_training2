package com.yuruneji.camera_training2.presentation.setting.home

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yuruneji.camera_training2.common.TextValidator
import com.yuruneji.camera_training2.databinding.FragmentSettingHomeBinding
import com.yuruneji.camera_training2.presentation.setting.view.DatePickerFragment
import com.yuruneji.camera_training2.presentation.setting.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class SettingHomeFragment : Fragment(), DatePickerFragment.OnSelectedDateListener,
    TimePickerFragment.OnSelectedTimeListener {

    companion object {
        fun newInstance() = SettingHomeFragment()
    }

    private var _binding: FragmentSettingHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        binding.btnDisplay.setOnClickListener {
            // findNavController().navigate(R.id.action_settingHomeFragment_to_settingDisplayFragment)
        }

        binding.btnCamera.setOnClickListener {
            // findNavController().navigate(R.id.action_settingHomeFragment_to_settingCameraFragment)
        }

        binding.text1.textLayout.hint = "ヒント"
        binding.text1.textLayout.counterMaxLength = 10
        binding.text1.textLayout.isCounterEnabled = true
        binding.text1.text.inputType=EditorInfo.TYPE_CLASS_TEXT
        binding.text1.text.text = Editable.Factory.getInstance().newEditable("abc")


        val userTextValidator = object : TextValidator(binding.userLayout, binding.user) {
            override fun validate(
                layout: TextInputLayout,
                editText: TextInputEditText,
                text: String?
            ) {
                if (text.isNullOrEmpty()) {
                    layout.error = "ユーザー名を入力してください"
                } else {
                    layout.error = null
                }
            }
        }
        binding.user.addTextChangedListener(userTextValidator)


        val array = arrayOf("未選択", "大阪", "名古屋", "東京")
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
        binding.spinner1.adapter = arrayAdapter

        val menuList = arrayListOf("Java", "Kotlin", "JavaScript", "TypeScript")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, menuList)
        binding.dropdown1.setAdapter(adapter)
        binding.dropdown1.setText(menuList[1], false)
        binding.dropdown1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })

        //     Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        // }

        binding.date.setOnClickListener {
            val datePicker = DatePickerFragment()
            datePicker.show(childFragmentManager, "datePicker")
        }

        binding.time.setOnClickListener {
            val timePicker = TimePickerFragment()
            timePicker.show(childFragmentManager, "timePicker")
        }

        binding.btnSave.setOnClickListener {
            val userLayout = binding.userLayout
            val user = binding.user.text.toString()


            val password = binding.password.text.toString()
            val spinner = binding.spinner1.selectedItem.toString()
            val dropdown = binding.dropdown1.text.toString()

        }

    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        Timber.i(Throwable().stackTrace[0].methodName)
        Timber.i("year: $year, month: $month, dayOfMonth: $dayOfMonth")

        val date = "$year/${month + 1}/$dayOfMonth"
        binding.date.setText(date)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        Timber.i(Throwable().stackTrace[0].methodName)
        Timber.i("hour: $hour, minute: $minute")

        val time = "$hour:$minute"
        binding.time.setText(time)
    }


}
