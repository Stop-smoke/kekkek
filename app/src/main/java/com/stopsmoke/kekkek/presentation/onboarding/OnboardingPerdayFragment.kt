package com.stopsmoke.kekkek.presentation.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stopsmoke.kekkek.databinding.FragmentOnboardingPerdayBinding


class OnboardingPerdayFragment : Fragment() {

    private var _binding: FragmentOnboardingPerdayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingPerdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnOnboardingNext.setOnClickListener {
//            findNavController().navigate(R.id.)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}