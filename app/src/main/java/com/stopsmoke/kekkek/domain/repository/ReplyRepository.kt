package com.stopsmoke.kekkek.domain.repository

import androidx.paging.PagingData
import com.stopsmoke.kekkek.domain.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRepository {
    suspend fun addReply(reply: Reply): Result<Unit>

    suspend fun getReply(postId: String, commentId: String): Flow<PagingData<Reply>>

    suspend fun getReplyCount(postId: String, commentId: String): Flow<Long>
}