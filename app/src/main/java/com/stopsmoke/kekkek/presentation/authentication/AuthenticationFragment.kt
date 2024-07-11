package com.stopsmoke.kekkek.presentation.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.User
import com.stopsmoke.kekkek.R
import com.stopsmoke.kekkek.authorization.google.GoogleAuthorization
import com.stopsmoke.kekkek.authorization.google.GoogleAuthorizationCallbackListener
import com.stopsmoke.kekkek.authorization.kakao.KakaoAuthorization
import com.stopsmoke.kekkek.authorization.kakao.KakaoAuthorizationCallbackListener
import com.stopsmoke.kekkek.databinding.FragmentAuthenticationBinding
import com.stopsmoke.kekkek.invisible
import com.stopsmoke.kekkek.presentation.collectLatestWithLifecycle
import com.stopsmoke.kekkek.presentation.onboarding.AuthenticationUiState
import com.stopsmoke.kekkek.presentation.onboarding.OnboardingViewModel
import com.stopsmoke.kekkek.presentation.snackbarLongShow
import com.stopsmoke.kekkek.visible

class AuthenticationFragment : Fragment(), KakaoAuthorizationCallbackListener,
    GoogleAuthorizationCallbackListener {

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding: FragmentAuthenticationBinding get() = _binding!!

    private val viewModel: OnboardingViewModel by activityViewModels()

    private var kakaoAuthorization: KakaoAuthorization? = null
    private var googleAuthorization: GoogleAuthorization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.invisible()
        googleAuthorization = GoogleAuthorization(this).apply {
            registerCallbackListener(this@AuthenticationFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.flLoginKakao.setOnClickListener {
            kakaoAuthorization = KakaoAuthorization().apply {
                registerCallbackListener(this@AuthenticationFragment)
                loginKakao(requireContext())
            }
        }

        binding.flLoginGoogle.setOnClickListener {
            googleAuthorization?.launchGoogleAuthActivity()
        }

        viewModel.isRegisteredUser.collectLatestWithLifecycle(lifecycle) {
            when (it) {
                is AuthenticationUiState.AlreadyUser -> {
                    findNavController().navigate("home") {
                        popUpTo(findNavController().graph.id) {
                            inclusive = true
                        }
                    }
                }

                is AuthenticationUiState.Init -> {

                }

                is AuthenticationUiState.NewMember -> {
                    findNavController().navigate("onboarding_graph")
                }

                is AuthenticationUiState.Error -> {
                    it.t?.printStackTrace()
                    Toast.makeText(requireContext(), "에러가 발생하였습니다", Toast.LENGTH_SHORT).show()
                }

                is AuthenticationUiState.Guest -> {
                    Toast.makeText(requireContext(), getString(R.string.guest_mode_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.visible()
        kakaoAuthorization?.unregisterCallbackListener()
        googleAuthorization?.unregisterCallbackListener()
        _binding = null
    }

    override fun onSuccess(token: OAuthToken, user: User) {
        val credential = oAuthCredential(KAKAO_PROVIDER_ID) {
            idToken = token.idToken
            accessToken = token.accessToken
        }
        val auth = Firebase.auth
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                viewModel.updateUid(authResult.user?.uid ?: "")
                viewModel.updateUserName(
                    authResult.user?.displayName ?: getString(R.string.login_default_nickname)
                )

                authResult.user?.displayName?.let {
                    Toast.makeText(requireContext(), "${it}님 환영합니다", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "로그인을 실패하였습니다", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }

    override fun onSuccess(user: FirebaseUser) {
        if (user.uid.isBlank()) {
            view?.snackbarLongShow("최근에 탈퇴한 유저로 확인됩니다\n30분 뒤에 다시 시도해 주세요")
            return
        }

        viewModel.updateUid(user.uid)
        viewModel.updateUserName(user.displayName ?: getString(R.string.login_default_nickname))
        Toast.makeText(requireContext(), "${user.displayName}님 환영합니다", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(t: Throwable?) {
        if (context != null) {
            Toast.makeText(context, "로그인 에러", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val KAKAO_PROVIDER_ID = "oidc.kakao"
    }
}