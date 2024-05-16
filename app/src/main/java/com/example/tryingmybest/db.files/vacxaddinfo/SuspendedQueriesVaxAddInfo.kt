package com.example.madness.connections.queries.vacxaddinfo


import android.util.Log
import com.example.tryingmybest.db.files.DBconnection
import com.example.tryingmybest.db.files.vacxaddinfo.VaxAddInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SuspendedQueriesVaxAddInfo {
    /**
     * Inserts vax additional info into the database
     */
    suspend fun insertVaxAddInfo(vaxAddInfo: VaxAddInfoData): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxAddInfoQueries = DBqueriesVacAddInfo(connection)
            return withContext(Dispatchers.IO) {
                vaxAddInfoQueries.insertVaxInfo(vaxAddInfo)
            }
        } catch (e: Exception) {
            println("Error inserting vax add info: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets all vax additional info from the database
     */
    suspend fun getAllVaxAddInfo(): Set<VaxAddInfoData> {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = DBconnection.getConnection() // It's safe to open connection here because we are already on the background thread
            try {
                val vaxAddInfoQueries = DBqueriesVacAddInfo(connection)
                val result = vaxAddInfoQueries.getAllVaxAddInfo() // Fetch all vaccine additional information
                result // Return the fetched data
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error getting all vax add info: ${e.message}", e)
                emptySet<VaxAddInfoData>()  // Return an empty set if there is an exception
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
     * Deletes vax additional info from the database
     */
    suspend fun deleteVaxAddInfo(vaxId: Int): Boolean {
        val connection = DBconnection.getConnection()
        try {
            val vaxAddInfoQueries = DBqueriesVacAddInfo(connection)
            return withContext(Dispatchers.IO) {
                vaxAddInfoQueries.deleteVaxInfo(vaxId)
            }
        } catch (e: Exception) {
            println("Error deleting vax add info: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets vax additional info from the database
     * @param vaxId (int)
     * @return object of VaxInfoData
     */
    suspend fun getVaxAddInfo(vaxId: Int): VaxAddInfoData {
        val connection = DBconnection.getConnection()
        try {
            val vaxAddInfoQueries = DBqueriesVacAddInfo(connection)
            return withContext(Dispatchers.IO) {
                vaxAddInfoQueries.getVaxAddInfo(vaxId)
            }
        } catch (e: Exception) {
            println("Error getting vax add info: ${e.message}")
            throw e
        } finally {
            connection.close()
        }
    }

    /**
     * Gets vax id from the database
     * @param vaxNameCompany (String)
     * @return vax id (int)
     */
    suspend fun getVaxId(vaxNameCompany: String): Int {
        return withContext(Dispatchers.IO) {  // Ensure that all operations within are executed on the IO thread
            val connection = DBconnection.getConnection() // It's safe to open connection here because we are already on the background thread
            try {
                val vaxAddInfoQueries = DBqueriesVacAddInfo(connection)
                vaxAddInfoQueries.getVaxId(vaxNameCompany) // Fetch the vax id
            } catch (e: Exception) {
                Log.e("DatabaseOperations", "Error getting vax id: ${e.message}", e)
                throw e  // Rethrow the exception to handle it outside if needed
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