package com.example.madness.connections.queries.user

import android.util.Log
import com.example.tryingmybest.db.files.DBconnection
import com.example.tryingmybest.db.files.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SuspendedQueriesUser {
    /**
     * Inserts a user into the database
     * @param user the user to be inserted
     * @return true if the user was inserted successfully, false otherwise
     */
    suspend fun insertUser(user: UserData): Boolean {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = DBconnection.getConnection() // It's safe to open connection here because we are already on the background thread
            try {
                val userQueries = DBqueriesUser(connection)
                val insertResult = userQueries.insertUser(user) // Perform the insert operation
                insertResult  // Return the result of the insert operation
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error inserting user: ${e.message}", e)
                false  // Return false if there is an exception
            } finally {
                try {
                    connection.close()  // Ensure the connection is closed after the operation
                } catch (e: Exception) {
                    Log.e("DatabaseOperations", "Error closing database connection: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Gets the user id of a user given its username
     * @param username the username of the user
     * @return the user id of the user, -1 if the user does not exist
     */
    suspend fun getUserId(username: String): Int {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = DBconnection.getConnection() // It's safe to open connection here because we are already on the background thread
            try {
                val userQueries = DBqueriesUser(connection)
                val userId = userQueries.getUserId(username) // Perform the get operation
                userId  // Return the result of the get operation
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error getting user ID: ${e.message}", e)
                -1  // Return -1 or any invalid ID if there is an exception
            } finally {
                try {
                    connection.close()  // Ensure the connection is closed after the operation
                } catch (e: Exception) {
                    Log.e("DatabaseOperations", "Error closing database connection: ${e.message}", e)
                }
            }
        }
    }
}