package com.example.madness.connections.queries.auth

import com.example.tryingmybest.db.files.auth.AuthData

interface AuthDAO {
    fun insertAuth(auth: AuthData): Boolean
    fun getAuth(mail: String, password: String): Int
    fun getRole(userId: Int): String
}