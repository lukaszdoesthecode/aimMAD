package com.example.madness.connections.queries.vacxaddinfo

import com.example.tryingmybest.db.files.vacxaddinfo.VaxAddInfoData
import java.sql.Connection

class DBqueriesVacAddInfo(private val connection: Connection): VaxAddInfoDAO{
    /**
     * Inserts a new VaxAddInfo into the database
     * @param vaxAddInfo the VaxAddInfo to be inserted
     * @return true if the VaxAddInfo was inserted successfully, false otherwise
     */
    override fun insertVaxInfo(vaxAddInfo: VaxAddInfoData): Boolean {
        val query = "CALL insert_vax_add_info(?, ?, ?)"
        val preparedStatement = connection.prepareCall(query)
        vaxAddInfo.vaxId?.let { preparedStatement.setInt(1, it) }
        preparedStatement.setString(2, vaxAddInfo.vaxNameCompany)
        preparedStatement.setString(3, vaxAddInfo.vaxDescription)

        val result = !preparedStatement.execute()
        preparedStatement.close()
        return result
    }

    /**
     * Deletes a VaxAddInfo from the database
     * @param vaxId the id of the VaxAddInfo to be deleted
     * @return true if the VaxAddInfo was deleted successfully, false otherwise
     */
    override fun deleteVaxInfo(vaxId: Int): Boolean {
        val query = "CALL delete_vax_add_info(?)"
        val preparedStatement = connection.prepareCall(query)
        preparedStatement.setInt(1, vaxId)
        return preparedStatement.executeUpdate() > 0
    }

    /**
     * Gets the VaxAddInfo of a VaxAddInfo
     * @param vaxId the id of the VaxAddInfo
     * @return the VaxAddInfo of the VaxAddInfo
     */
    override fun getVaxAddInfo(vaxId: Int): VaxAddInfoData {
        val query = "CALL get_vax_add_info(?)"
        connection.prepareCall(query).use { preparedStatement ->
            preparedStatement.setInt(1, vaxId)
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return VaxAddInfoData(
                    resultSet.getInt("vax_add_info_id"),
                    resultSet.getString("vax_company"),
                    resultSet.getString("vax_description")
                )
            } else {
                throw NoSuchElementException("VaxAddInfo not found with vaxId: $vaxId")
            }
        }
    }

    /**
     * Gets the VaxAddInfo of a VaxAddInfo
     * @param vaxNameCompany the name of the VaxAddInfo
     * @return the VaxAddInfo of the VaxAddInfo
     */
    override fun getVaxId(vaxNameCompany: String): Int {
        val query = "CALL get_vax_id(?)"
        connection.prepareCall(query).use { preparedStatement ->
            preparedStatement.setString(1, vaxNameCompany)
            val resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt("vax_add_info_id")
            } else {
                throw NoSuchElementException("VaxAddInfo not found with vaxNameCompany: $vaxNameCompany")
            }
        }
    }

    /**
     * Gets all VaxAddInfo from the database
     * @return a set of all VaxAddInfo
     */
    override fun getAllVaxAddInfo(): Set<VaxAddInfoData> {
        val query = "CALL get_all_vax_add_info()"
        connection.prepareCall(query).use { preparedStatement ->
            val resultSet = preparedStatement.executeQuery()
            val vaxAddInfoSet = mutableSetOf<VaxAddInfoData>()
            while (resultSet.next()) {
                vaxAddInfoSet.add(
                    VaxAddInfoData(
                        resultSet.getInt("vax_add_info_id"),
                        resultSet.getString("vax_company"),
                        resultSet.getString("vax_description")
                    )
                )
            }
            return vaxAddInfoSet
        }
    }
}