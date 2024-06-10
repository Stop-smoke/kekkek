package com.stopsmoke.kekkek.presentation.achievement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.stopsmoke.kekkek.common.Result
import com.stopsmoke.kekkek.domain.model.Achievement
import com.stopsmoke.kekkek.domain.repository.AchievementRepository
import com.stopsmoke.kekkek.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import org.checkerframework.checker.units.qual.Current
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    achievementRepository: AchievementRepository,
    userRepository: UserRepository,
) : ViewModel() {
    val currentProgressItem : CurrentProgress = getCurrentProgress()

    val achievements: Flow<PagingData<AchievementItem>> =
        achievementRepository.getAchievementItems().let {
            when (it) {
                is Result.Error -> {
                    it.exception?.printStackTrace()
                    emptyFlow()
                }

                is Result.Loading -> emptyFlow()
                is Result.Success -> it.data.map { pagingData ->
                    pagingData.map {
                        it.getItem()
                    }
                }
            }
        }.cachedIn(viewModelScope)

    private fun Achievement.getItem() = AchievementItem(
        id = id,
        name = name,
        content = content,
        image = image,
        category = category,
        maxProgress = maxProgress,
    )

    private fun getCurrentProgress() = CurrentProgress(
        time = 0,
        comment = 0,
        post = 0,
        rank = 0,
        achievement = 0
    )
}