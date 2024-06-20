package com.stopsmoke.kekkek.presentation.popularWritingList

import com.stopsmoke.kekkek.presentation.community.CommunityWritingItem

data class PopularWritingListUiState (
    val list: List<CommunityWritingItem>,
    val isLoading: Boolean =false
) {
    companion object {
        fun init() = PopularWritingListUiState(
            list = emptyList()
        )
    }
}
