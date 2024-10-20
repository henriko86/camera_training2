package com.yuruneji.camera_training.presentation.login

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.yuruneji.camera_training.R
import com.yuruneji.camera_training.databinding.FragmentLoginBinding
import com.yuruneji.camera_training.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        // fun newInstance() = LoginFragment()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        // Eメール
        binding.emailEdit.addTextChangedListener { s ->
            // val email = s.toString()
            // viewModel.setEmail(email)
        }

        // パスワード
        binding.passwordEdit.addTextChangedListener { s ->
            // val password = s.toString()
            // viewModel.setPassword(password)
        }

        // ログインボタン
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            // viewModel.login(email, password)

            findNavController().navigate(R.id.action_LoginFragment_to_SettingFragment)
        }

    }

    override fun onDestroyView() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onDestroyView()
        _binding = null
    }
}
