package com.stopsmoke.kekkek.presentation.myCommentList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopsmoke.kekkek.R
import com.stopsmoke.kekkek.databinding.FragmentMyCommentBinding
import com.stopsmoke.kekkek.invisible
import com.stopsmoke.kekkek.presentation.community.CommunityCallbackListener
import com.stopsmoke.kekkek.presentation.community.CommunityWritingItem
import com.stopsmoke.kekkek.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyCommentFragment : Fragment() {

    private var _binding: FragmentMyCommentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyCommentViewModel by viewModels()

    private val listAdapter: MyCommentListAdapter by lazy {
        MyCommentListAdapter(viewModel)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        initAppBar()

        rvMyComment.adapter = listAdapter
        rvMyComment.layoutManager = LinearLayoutManager(requireContext())

        viewModel.updateUserState()
        initListAdapterCallback()
    }

    private fun initListAdapterCallback() {
        listAdapter.registerCallbackListener(
            object : CommunityCallbackListener {
                override fun navigateToUserProfile(uid: String) {}

                override fun navigateToPost(communityWritingItem: CommunityWritingItem) {
                    findNavController().navigate(
                        resId = R.id.action_myCommentList_to_postView,
                        args = bundleOf("item" to communityWritingItem)
                    )
                }
            }
        )
    }

    private fun initAppBar() {
        val ivMyCommentBack =
            requireActivity().findViewById<ImageView>(R.id.iv_my_comment_back)

        ivMyCommentBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initViewModel() = with(viewModel) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            myCommentPosts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { myComments ->
                    listAdapter.submitData(myComments)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.invisible()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        listAdapter.unregisterCallbackListener()
        activity?.visible()
    }
}