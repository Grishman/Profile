package com.grishman.profiletest.model

data class ProfileResponse(
        val firstName: String?,
        val lastName: String?,
        val location: ProfileLocation?,
        val avatar: Avatar?)
