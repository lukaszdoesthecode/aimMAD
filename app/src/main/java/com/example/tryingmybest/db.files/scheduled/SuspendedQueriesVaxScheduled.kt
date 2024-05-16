package com.example.tryingmybest.db.files.scheduled

import android.util.Log
import com.example.tryingmybest.db.files.DBconnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SuspendedQueriesVaxScheduled {
    suspend fun insertVaxScheduled(scheduledVax: VaxScheduledData): Boolean {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = DBconnection.getConnection() // It's safe to open connection here because we are already on the background thread
            Log.d("DatabaseOperations", "Database connection established.")

            try {
                val scheduledQueries = DBqueriesVaxScheduled(connection)
                Log.d("DatabaseOperations", "DBqueriesVaxScheduled instance created.")

                val insertResult = scheduledQueries.insertVaxScheduled(scheduledVax) // Perform the insert operation
                Log.d("DatabaseOperations", "Insert operation performed. Result: $insertResult")

                insertResult  // Return the result of the insert operation
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error inserting scheduled vaccination: ${e.message}", e)
                false  // Return false if there is an exception
            } finally {
                try {
                    connection.close()  // Ensure the connection is closed after the operation
                    Log.d("DatabaseOperations", "Database connection closed.")
                } catch (e: Exception) {
                    Log.e("DatabaseOperations", "Error closing database connection: ${e.message}", e)
                }
            }
        }
    }
}
