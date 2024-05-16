package com.example.madness.connections.queries.user

import com.example.tryingmybest.db.files.user.UserData

/**
 * Interface representing the possible queries for the User entity

 */
interface UserDAO {
    fun insertUser(user: UserData): Boolean
    fun getUserId(username: String): Int
}