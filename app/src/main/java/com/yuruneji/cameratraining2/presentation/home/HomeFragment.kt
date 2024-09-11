package com.yuruneji.cameratraining2.presentation.home

import android.os.Bundle
import android.text.Editable
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // binding.btnCamerax.setOnClickListener {
        //     findNavController().navigate(R.id.action_home_to_camera)
        // }

        // binding.btnLogView.setOnClickListener {
        //     findNavController().navigate(R.id.action_home_to_log_view)
        // }

        binding.password.text = Editable.Factory.getInstance().newEditable("xxxxx")
        binding.password.addTextChangedListener {
            Timber.d(it.toString())
        }

        val array = arrayOf("未選択", "大阪", "名古屋", "東京")
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, array)
        binding.spinner1.adapter = arrayAdapter


        // // Adapter作成
        // val menuList = arrayListOf("Java", "Kotlin", "JavaScript", "TypeScript")
        // val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, menuList)
        // // Adapter登録
        // binding.dropdown1.adapter.setAdapter(adapter)
        // binding.dropdown1.adapter.text = menuList[0]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()
    }

    override fun onPause() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onPause()
    }

    override fun onStop() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

}
