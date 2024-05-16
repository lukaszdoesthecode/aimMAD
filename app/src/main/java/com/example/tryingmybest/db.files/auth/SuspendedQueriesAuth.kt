package com.example.madness.connections.queries.auth

import android.util.Log
import com.example.tryingmybest.db.files.DBconnection
import com.example.tryingmybest.db.files.auth.AuthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.SQLException

object SuspendedQueriesAuth {
    private const val TAG = "SuspendedQueriesAuth"

    suspend fun insertAuth(auth: AuthData): Boolean {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = try {
                DBconnection.getConnection()  // Attempt to open a database connection
            } catch (e: Exception) {
                Log.e(TAG, "Failed to obtain database connection: ${e.message}", e)
                return@withContext false  // Return early with false if connection fails
            }

            try {
                Log.d(TAG, "Starting to insert authentication data")
                val authQueries = DBqueriesAuth(connection)
                authQueries.insertAuth(auth)  // Perform the insert operation
                Log.d(TAG, "Auth inserted successfully")
                true  // Return true to indicate success
            } catch (e: SQLException) {
                Log.e(TAG, "SQLException occurred while inserting auth: ${e.message}", e)
                false  // Return false to indicate SQL failure
            } catch (e: Exception) {
                Log.e(TAG, "General exception occurred while inserting auth: ${e.message}", e)
                false  // Return false to indicate general failure
            } finally {
                try {
                    connection.close()  // Attempt to close the database connection
                    Log.d(TAG, "Connection closed successfully")
                } catch (e: SQLException) {
                    Log.e(TAG, "SQLException during connection close: ${e.message}", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during connection close: ${e.message}", e)
                }
            }
        }
    }

    suspend fun getAuth(mail: String, password: String): Int {
        return withContext(Dispatchers.IO) {  // Ensures execution on the IO dispatcher
            val connection = try {
                DBconnection.getConnection()  // Attempt to open a database connection
            } catch (e: Exception) {
                Log.e(TAG, "Failed to obtain database connection: ${e.message}", e)
                return@withContext -1  // Return -1 if connection fails
            }

            try {
                Log.d(TAG, "Starting to fetch authentication data")
                val query = "CALL get_auth(?, ?)"
                val preparedStatement = connection.prepareCall(query)
                preparedStatement.setString(1, mail)
                preparedStatement.setString(2, password)

                val resultSet = preparedStatement.executeQuery()
                if (resultSet.next()) {
                    resultSet.getInt("user_id")  // Return the user ID if found
                } else {
                    -1  // Return -1 if no record is found
                }
            } catch (e: SQLException) {
                Log.e(TAG, "SQLException occurred while fetching auth: ${e.message}", e)
                -1  // Return -1 to indicate SQL failure
            } catch (e: Exception) {
                Log.e(TAG, "General exception occurred while fetching auth: ${e.message}", e)
                -1  // Return -1 to indicate general failure
            } finally {
                try {
                    connection.close()  // Attempt to close the database connection
                    Log.d(TAG, "Connection closed successfully")
                } catch (e: SQLException) {
                    Log.e(TAG, "SQLException during connection close: ${e.message}", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during connection close: ${e.message}", e)
                }
            }
        }
    }

    suspend fun getRole(): String{
        return withContext(Dispatchers.IO) {
            val connection = try {
                DBconnection.getConnection()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to obtain database connection: ${e.message}", e)
                return@withContext ""
            }

            try {
                Log.d(TAG, "Starting to fetch role data")
                val query = "CALL get_auth_role(?)"
                val preparedStatement = connection.prepareCall(query)
                preparedStatement.setInt(1, 1)

                val resultSet = preparedStatement.executeQuery()
                if (resultSet.next()) {
                    resultSet.getString("role")
                } else {
                    ""
                }
            } catch (e: SQLException) {
                Log.e(TAG, "SQLException occurred while fetching role: ${e.message}", e)
                ""
            } catch (e: Exception) {
                Log.e(TAG, "General exception occurred while fetching role: ${e.message}", e)
                ""
            } finally {
                try {
                    connection.close()
                    Log.d(TAG, "Connection closed successfully")
                } catch (e: SQLException) {
                    Log.e(TAG, "SQLException during connection close: ${e.message}", e)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during connection close: ${e.message}", e)
                }
            }
        }
    }

}

