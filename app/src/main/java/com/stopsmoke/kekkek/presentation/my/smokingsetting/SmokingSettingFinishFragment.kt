//package com.stopsmoke.kekkek.presentation.my.smokingsetting
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.stopsmoke.kekkek.R
//import com.stopsmoke.kekkek.databinding.FragmentSmokingSettingFinishBinding
//import com.stopsmoke.kekkek.invisible
//import com.stopsmoke.kekkek.presentation.collectLatestWithLifecycle
//import com.stopsmoke.kekkek.presentation.snackbarLongShow
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SmokingSettingFinishFragment : Fragment() {
//    private var _binding: FragmentSmokingSettingFinishBinding? = null
//    private val binding get() = _binding!!
//
//    private val viewModel: SmokingSettingViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentSmokingSettingFinishBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//        observeUiState()
//    }
//
//    private fun observeUiState() {
//        viewModel.resultUserSettings.collectLatestWithLifecycle(lifecycle) {
//            when(it) {
//                is SmokingSettingUiState.Error -> {
//                    view?.snackbarLongShow("error")
//                    findNavController().navigate(R.id.action_resetting_onboarding_smoking_finish_to_my_page)
//                }
//                is SmokingSettingUiState.Loading -> {}
//                is SmokingSettingUiState.Success -> {
//                    findNavController().navigate(R.id.action_resetting_onboarding_smoking_finish_to_my_page)
//                }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        activity?.invisible()
//    }
//
//    private fun initView() = with(binding) {
//        btnResetingOnboardingNext.setOnClickListener {
//            viewModel.updateUserConfig()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}