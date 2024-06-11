package com.stopsmoke.kekkek.domain.model

import com.stopsmoke.kekkek.domain.getElapsedDateTime
import java.time.LocalDateTime

data class Post(
    val id: String,
    val commentId: String,
    val written: Written,
    val title: String,
    val text: String,
    val dateTime: DateTime,
    val likeUser: List<String>,
    val unlikeUser: List<String>,
    val categories: PostCategory,
    val views: Long,
    val commentUser: List<String>
    // Todo: proileImage 변수 추가 필요
) {
    val modifiedElapsedDateTime = dateTime.modified.getElapsedDateTime()

    companion object {
        fun emptyPost() = Post(
            id = "",
            commentId = "",
            written = Written(
                uid = "",
                name = "",
                profileImage = ProfileImage.Default,
                ranking = 99999
            ),
            title = "",
            text = "",
            dateTime = DateTime(
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ),
            likeUser = emptyList(),
            unlikeUser = emptyList(),
            categories = PostCategory.UNKNOWN,
            views = 0,
            commentUser = emptyList()
        )
    }
}