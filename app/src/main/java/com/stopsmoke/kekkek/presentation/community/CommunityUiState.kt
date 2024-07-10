package com.stopsmoke.kekkek.presentation.community

sealed interface CommunityUiState {
    data class CommunityNormalUiState(
        val popularItem: CommunityPopularItem,
        val isLoading: Boolean = false
    ) : CommunityUiState

    data object ErrorExit: CommunityUiState

    companion object {
        fun init() = CommunityNormalUiState(
            popularItem = CommunityPopularItem(
                postInfo1 = emptyCommunityWritingListItem(),
                postInfo2 = emptyCommunityWritingListItem()
            ),
        )
    }
}

