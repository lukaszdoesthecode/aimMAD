package com.example.madness.connections.vaccinations


import android.util.Log
import com.example.tryingmybest.db.files.DBconnection
import com.example.tryingmybest.db.files.vaccinations.VaccinationsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SuspendedQueriesVaccinations {
    /**
     * Inserts a new vaccination into the database
     * @param vax the vaccination to be inserted
     * @return true if the vaccination was inserted successfully, false otherwise
     */
    suspend fun insertVaccination(vax: VaccinationsData): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.insertVaccination(vax)
            }
        } catch (e: Exception) {
            println("Error inserting vaccination: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Deletes a vaccination from the database
     * @param vaxId the id of the vaccination to be deleted
     * @return true if the vaccination was deleted successfully, false otherwise
     */
    suspend fun deleteVaccination(vaxId: Int): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.deleteVaccination(vaxId)
            }
        } catch (e: Exception) {
            println("Error deleting vaccination: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets the number of doses of a vaccination
     * @param vaxId the id of the vaccination
     * @return the number of doses of the vaccination
     */
    suspend fun getNumberOfDoses(vaxId: Int): Int {
        return withContext(Dispatchers.IO) {  // Execute all IO operations on a background thread
            val connection = DBconnection.getConnection()  // Open the connection inside the IO context
            try {
                val vaxQueries = DBqueriesVaccinations(connection)
                vaxQueries.getNumberOfDoses(vaxId)  // Fetch the number of doses directly
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error getting number of doses: ${e.message}", e)
                throw e  // Rethrow the exception for handling it further up the call stack
            } finally {
                try {
                    connection.close()  // Make sure to close the connection
                } catch (e: Exception) {
                    Log.e("DatabaseOperations", "Error closing database connection: ${e.message}", e)
                }
            }
        }
    }


    /**
     * Gets the time between doses of a vaccination
     * @param vaxId the id of the vaccination
     * @return the time between doses of the vaccination
     */
    suspend fun getTimeBetweenDoses(vaxId: Int): Int {
        return withContext(Dispatchers.IO) {  // Execute all IO operations on a background thread
            val connection = DBconnection.getConnection()  // Open the connection inside the IO context
            try {
                val vaxQueries = DBqueriesVaccinations(connection)
                vaxQueries.getTimeBetweenDoses(vaxId)  // Fetch the time between doses directly
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error getting time between doses: ${e.message}", e)
                throw e  // Rethrow the exception for handling it further up the call stack
            } finally {
                try {
                    connection.close()  // Make sure to close the connection
                } catch (e: Exception) {
                    Log.e("DatabaseOperations", "Error closing database connection: ${e.message}", e)
                }
            }
        }
    }


    /**
     * Updates the number of doses of a vaccination
     * @param vaxId the id of the vaccination
     * @param noOfDoses the new number of doses
     * @return true if the number of doses was updated successfully, false otherwise
     */
    suspend fun updateNoOfDosesVaccination(vaxId: Int, noOfDoses: Int): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.updateNoOfDosesVaccination(vaxId, noOfDoses)
            }
        } catch (e: Exception) {
            println("Error updating number of doses: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Updates the time between doses of a vaccination
     * @param vaxId the id of the vaccination
     * @param timeBetweenDoses the new time between doses
     * @return true if the time between doses was updated successfully, false otherwise
     */
    suspend fun updateTimeBetweenDosesVaccination(vaxId: Int, timeBetweenDoses: Int): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.updateTimeBetweenDosesVaccination(vaxId, timeBetweenDoses)
            }
        } catch (e: Exception) {
            println("Error updating time between doses: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets a vaccination given its id
     * @param vaxId the id of the vaccination
     * @return the vaccination with the given id
     */
    suspend fun getVaccination(vaxId: Int): VaccinationsData? {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.getVaccination(vaxId)
            }
        } catch (e: Exception) {
            println("Error getting vaccination: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets all vaccinations
     * @return a set containing all vaccinations
     */
    suspend fun getAllVaccinations(): Set<VaccinationsData?>? {
        val connection = DBconnection.getConnection()
        try {
            val vaxQueries = DBqueriesVaccinations(connection)
            return withContext(Dispatchers.IO) {
                vaxQueries.getAllVaccinations()
            }
        } catch (e: Exception) {
            println("Error getting all vaccinations: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }
}