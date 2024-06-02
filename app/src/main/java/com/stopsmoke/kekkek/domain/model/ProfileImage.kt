package com.stopsmoke.kekkek.domain.model

sealed interface ProfileImage {

    data object Default : ProfileImage

    data class Web(val url: String): ProfileImage
}