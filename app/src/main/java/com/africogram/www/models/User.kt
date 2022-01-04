package com.africogram.www.models

/**
 * User Model Class
 */
data class User(
    val userId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val birthDay: Int? = null,
    val birthMonth: Int? = null,
    val birthYear: Int? = null,
    val gender: String? = null,
    val profilePic: String? = null,
    val lastOnlineDate: String? = null,
    val lastOnlineTime: Long? = null
)