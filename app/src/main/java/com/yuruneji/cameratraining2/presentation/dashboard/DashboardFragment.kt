package com.yuruneji.cameratraining2.presentation.dashboard

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentDashboardBinding
import com.yuruneji.cameratraining2.presentation.dashboard.view.DatePickerFragment
import com.yuruneji.cameratraining2.presentation.dashboard.view.TimePickerFragment
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

class DashboardFragment : Fragment(),
    DatePickerFragment.OnSelectedDateListener,
    TimePickerFragment.OnSelectedTimeListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        val backBtn = binding.btnBack
        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_home)
        }

        binding.pickDate.setOnClickListener {
            DatePickerFragment().show(childFragmentManager, "datePicker")
        }

        binding.pickTime.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedDate(year: Int, month: Int, dayOfMonth: Int) {
        binding.dateText.text =
            String.format(Locale.JAPAN, "%04d/%02d/%02d", year, month + 1, dayOfMonth)
    }

    override fun selectedTime(hour: Int, minute: Int) {
        binding.timeText.text = String.format(Locale.JAPAN, "%02d:%02d", hour, minute)
    }

    // override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    //     val str = String.format(Locale.JAPAN, "%d:%d", hourOfDay, minute)
    //
    //     Timber.i(str)
    //
    //     lifecycleScope.launch {
    //         binding.timeText.text = str
    //     }
    // }
}
