package com.yuruneji.cameratraining2.presentation.logview

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentCameraBinding
import com.yuruneji.cameratraining2.databinding.FragmentLogViewBinding
import com.yuruneji.cameratraining2.domain.usecase.ListViewItem
import com.yuruneji.cameratraining2.domain.usecase.LogViewAdapter
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogViewFragment : Fragment() {

    companion object {
        fun newInstance() = LogViewFragment()
    }

    private var _binding: FragmentLogViewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogViewViewModel by viewModels()

    private lateinit var adapter: LogViewAdapter
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogViewBinding.inflate(inflater, container, false)

        listView = binding.listview

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_log_view_to_home)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val today = LocalDateTime.now()
        val fileName =
            "${today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.log"
        val logFile = File(requireContext().filesDir, fileName)

        val listItem = mutableListOf<ListViewItem>()
        try {
            FileReader(logFile).use { reader ->
                for (line in reader.readLines()) {
                    listItem.add(ListViewItem("", line))
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        adapter = LogViewAdapter(requireContext(), listItem)
        listView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView()")
        _binding = null
    }
}
