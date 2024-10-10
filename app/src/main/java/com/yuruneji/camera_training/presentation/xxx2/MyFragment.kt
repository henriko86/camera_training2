package com.yuruneji.camera_training.presentation.xxx2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuruneji.camera_training.databinding.FragmentMyBinding
import timber.log.Timber

class MyFragment : Fragment() {

    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        // Create my list.
        val myList = mutableListOf<MyData>()
        for (i in 1..10) {
            myList.add(MyData("item$i"))
        }

        // Setting for recycler view.
        val adapter = MyAdapter(myList)
        binding.rcvSample.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvSample.adapter = adapter

        // Activate Item touch helper.
        val helper = ItemTouchHelper(MyItemTouchHelper.getCallback(myList))
        binding.swtCanSort.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                helper.attachToRecyclerView(binding.rcvSample)
            } else {
                helper.attachToRecyclerView(null)
            }
        }
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
