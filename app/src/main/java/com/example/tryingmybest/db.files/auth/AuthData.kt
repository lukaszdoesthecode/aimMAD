package com.example.tryingmybest.db.files.auth

import com.example.tryingmybest.db.files.entities.Role

/**
 * Class representing the AuthData entity
 */

data class AuthData(
    var userId: Int,
    var mail: String? = null,
    var password: String? = null,
    var role: String? = null
)
