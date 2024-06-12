package com.stopsmoke.kekkek.presentation.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.stopsmoke.kekkek.databinding.ItemCommunityPostwritingBinding
import com.stopsmoke.kekkek.domain.model.DateTimeUnit
import com.stopsmoke.kekkek.domain.model.ElapsedDateTime
import com.stopsmoke.kekkek.getRelativeTime

class CommunityListAdapter :
    PagingDataAdapter<CommunityWritingItem, CommunityListAdapter.ViewHolder>(diffUtil) {


    private var callback: CommunityCallbackListener? = null

    fun registerCallbackListener(callback: CommunityCallbackListener) {
        this.callback = callback
    }

    fun unregisterCallbackListener() {
        callback = null
    }


    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: CommunityWritingItem)
    }

    class WritingPostViewHolder(
        private val binding: ItemCommunityPostwritingBinding,
        private val callback: CommunityCallbackListener?,
    ) : ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnClickListener {
//                    val post = CommunityWritingItem(
//                        userInfo = UserInfo(
//                            name = tvItemWritingName.text.toString(),
//                            rank = tvItemWritingRank.text.to,
//                            profileImage = circleIvItemWritingProfile
//                        ),
//                        postInfo = PostInfo(
//                            title = tvItemWritingTitle.text.toString(),
//                            postType = "",
//                            view = tvItemWritingViewNum.text,
//                            like = tvItemWritingLikeNum,
//                            comment = tvItemWritingCommentNum
//                        ),
//                        postImage = "",
//                        post = tvItemWritingPost,
//                        postTime = tvItemWritingTimeStamp
//                    )
                }
            }
        }

        override fun bind(item: CommunityWritingItem): Unit = with(binding) {
            item.postInfo.let {
                tvItemWritingTitle.text = it.title
                tvItemWritingViewNum.text = it.view.toString()
                tvItemWritingLikeNum.text = it.like.toString()
                tvItemWritingCommentNum.text = it.comment.toString()
            }

            tvItemWritingPost.text = item.post
            tvItemWritingTimeStamp.text = getRelativeTime(item.postTime)

            item.userInfo.let {
                if (item.postImage != "") {
                    ivItemWritingPostImage.load(it.profileImage) {
                        crossfade(true)
//                    placeholder(R.drawable.placeholder) 로딩중 띄우나?
//                    error(R.drawable.error) 오류시 띄우나?
                    }
                } else {
                    ivItemWritingPostImage.visibility = View.GONE
                    setMarginEnd(tvItemWritingTitle, 16)
                }

                tvItemWritingName.text = it.name
                tvItemWritingRank.text = "랭킹 ${it.rank}위"
            }

            binding.circleIvItemWritingProfile.setOnClickListener {
                callback?.navigateToUserProfile(item.userInfo.uid)
            }

            binding.root.setOnClickListener {
                callback?.navigateToPost(item)
            }
        }

        private fun setMarginEnd(view: TextView, end: Int) {
            val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginEnd = end
            view.layoutParams = layoutParams
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommunityListAdapter.ViewHolder =
        WritingPostViewHolder(
            ItemCommunityPostwritingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            callback
        )


    override fun onBindViewHolder(holder: CommunityListAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CommunityWritingItem>() {
            override fun areItemsTheSame(
                oldItem: CommunityWritingItem,
                newItem: CommunityWritingItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CommunityWritingItem,
                newItem: CommunityWritingItem,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}