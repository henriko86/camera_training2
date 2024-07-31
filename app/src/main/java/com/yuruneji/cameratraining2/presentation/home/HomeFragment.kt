package com.yuruneji.cameratraining2.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yuruneji.cameratraining2.App
import com.yuruneji.cameratraining2.R
import com.yuruneji.cameratraining2.common.DataProvider
import com.yuruneji.cameratraining2.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        // fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var dataProvider: DataProvider

    val connectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onAttach(context: Context) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onCreate(savedInstanceState)

        Timber.i("---")
        Timber.i("${App.appContext}")
        Timber.i("---")


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i(Throwable().stackTrace[0].methodName)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val title: TextView = binding.title
        viewModel.title.observe(viewLifecycleOwner) {
            title.text = it
        }

        binding.clock.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s} ${start} ${count} ${after}")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s} ${start} ${before} ${count}")

                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
                val connectionType = activeNetwork?.type

                if (isConnected) {
                    binding.networkStatus.text = when (connectionType) {
                        ConnectivityManager.TYPE_WIFI -> "Wifiに接続しています"
                        ConnectivityManager.TYPE_MOBILE -> "モバイル通信に接続しています"
                        else -> "その他のネットワークに接続しています"
                    }
                } else {
                    binding.networkStatus.text = "インターネットに接続していません"
                }
                // Timber.i("networkInfo:${networkInfo == null}")
            }

            override fun afterTextChanged(s: Editable?) {
                // Timber.i("${Throwable().stackTrace[0].methodName} ${getThreadName()} ${s}")
            }
        })

        val cameraRadioGroup = binding.cameraRadioGroup
        val list: List<String> = listOf("None", "RGB", "IR")
        list.forEach {
            val radioButton = RadioButton(requireContext())
            radioButton.text = it
            radioButton.setPadding(0, 30, 0, 30)
            Timber.d("${radioButton.id}")

            cameraRadioGroup.addView(radioButton) // radioGroupにradioButtonを追加する
            // if (it.equals(list[0])) {
            //     radioButton.isChecked = true
            // }
        }
        cameraRadioGroup.forEach {
            Timber.d("RadioGroup:${cameraRadioGroup.id}:RadioButton:${it.id}")
        }
        cameraRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            Timber.i("checkedId=${checkedId}")
        }

// val        listView = view!!.findViewById(com.yuruneji.cameratraining2.R.id.listMemo)
//         // Entity的なデータをここで用意する
//         // ArrayAdapterを継承したadapterクラスを用意しsetAdapterでListViewにセットする
//         val adapter: CustomArrayAdapter = CustomArrayAdapter(
//             activity,
//             android.R.layout.simple_list_item_1,
//             listViewData
//         )
//         listView.setAdapter(adapter)
        var mylist = arrayOf("CPU", "memory", "Mouse")
        var listAdapter = ArrayAdapter(requireContext(), R.layout.list_item, R.id.text_view, mylist)
        binding.listView.adapter = listAdapter

        binding.btnCamerax.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera)
        }

        binding.btnCamera2.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_camera2)
        }

        binding.btnSetting.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_setting_login)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onViewCreated(view, savedInstanceState)

        // State
        lifecycleScope.launch {
            viewModel.state.collect { mainState ->
                var str = ""
                mainState.faceAuth?.let {
                    str += it.rect
                }

                mainState.error?.let {
                    str += it
                }

            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                viewModel.authState.collect { authState ->
                    var str = ""

                    if (authState.isFaceAuth && authState.isCardAuth && authState.isPostProcessAuth) {
                        str += "カード認証成功\n顔認証成功\n認証後処理成功"

                        viewModel.authStatusReset()
                    } else if (authState.isFaceAuth && authState.isCardAuth) {
                        str += "カード認証成功\n顔認証成功"

                        viewModel.postProcess()
                    } else if (authState.isCardAuth) {
                        str += "カード認証成功"
                    } else {
                        //
                    }
                }
            }
        }
    }

    override fun onStart() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onStart()
    }

    override fun onResume() {
        Timber.i(Throwable().stackTrace[0].methodName)
        super.onResume()

        // ドロップダウンの初期設定
        val dropdown: AutoCompleteTextView = binding.dropdownItem
        initDropdown(dropdown)
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

    private fun getThreadName(): String {
        return Thread.currentThread().name
    }

    // ドロップダウンの初期設定
    private fun initDropdown(dropdown: AutoCompleteTextView) {
        // 「R.array.foodType_array」は「values/array.xml」で定義
        val foodTypes = resources.getStringArray(R.array.cameraType_array)
        // 「R.layout.dropdown_item」は「vlayout/dropdown_item.xml」で定義
        val arrayAdapter =
            ArrayAdapter(dropdown.rootView.context, R.layout.dropdown_item, foodTypes)
        dropdown.setAdapter(arrayAdapter)
    }
}
