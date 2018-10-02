package com.grishman.profiletest.model

import android.location.Location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Profile {
    @SerializedName("firstName")
    @Expose
    private val firstName: String? = null
    @SerializedName("lastName")
    @Expose
    private val lastName: String? = null
    @SerializedName("location")
    @Expose
    private val location: Location? = null
    @SerializedName("avatar")
    @Expose
    private val avatar: Avatar? = null
}
