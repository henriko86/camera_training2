package com.yuruneji.camera_training.presentation.setting_base

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.FragmentSettingBaseBinding
import com.yuruneji.camera_training.databinding.FragmentSettingBinding
import com.yuruneji.camera_training.presentation.view.DatePickerFragment
import com.yuruneji.camera_training.presentation.view.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingBaseFragment : Fragment(), DatePickerFragment.OnSelectedDateListener, TimePickerFragment.OnSelectedTimeListener {

    companion object {
        fun newInstance() = SettingBaseFragment()
    }

    private var _binding: FragmentSettingBaseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingBaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i(Throwable().stackTrace[0].methodName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentSettingBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        setupDropDown()
        setupBindingEvent()
        observeFormChanges()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
        setupListeners()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()
        stopListeners()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        Timber.i(Throwable().stackTrace[0].methodName)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        Timber.i(Throwable().stackTrace[0].methodName)
    }

    private fun setupDropDown() {

    }

    private fun setupBindingEvent() {

    }

    private fun observeFormChanges() {
        //
    }

    private fun setupListeners() {
        //
    }

    private fun stopListeners() {

    }

}
