package com.example.tryingmybest.db.files.scheduled

import android.util.Log

class DBqueriesVaxScheduled(private val connection: java.sql.Connection) : VaxScheduledDAO {
    override fun insertVaxScheduled(vaxScheduled: VaxScheduledData): Boolean {
        val query = "CALL insert_vax_scheduled(?, ?, ?, ?)"
        Log.d("DatabaseOperations", "Preparing query: $query")

        val preparedStatement = connection.prepareCall(query)
        try {
            Log.d("DatabaseOperations", "Setting parameters for the query.")
            preparedStatement.setInt(1, vaxScheduled.vaxId)
            preparedStatement.setInt(2, vaxScheduled.vaxUserId)
            preparedStatement.setDate(3, vaxScheduled.vaxDateOfFirstDose)
            preparedStatement.setString(4, vaxScheduled.vaxStatus.toString())

            Log.d("DatabaseOperations", "Executing the query.")
            val result = !preparedStatement.execute()
            Log.d("DatabaseOperations", "Query executed. Result: $result")

            return result
        } catch (e: Exception) {
            Log.e("DatabaseOperations", "Error executing query: ${e.message}", e)
            return false
        } finally {
            try {
                preparedStatement.close()
                Log.d("DatabaseOperations", "PreparedStatement closed.")
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error closing PreparedStatement: ${e.message}", e)
            }
        }
    }
}
