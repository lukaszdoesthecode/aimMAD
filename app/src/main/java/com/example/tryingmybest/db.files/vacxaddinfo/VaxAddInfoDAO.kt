package com.example.madness.connections.queries.vacxaddinfo

import com.example.tryingmybest.db.files.vacxaddinfo.VaxAddInfoData

/**
 * Interface representing the possible queries for the VaxAddInfo entity
 */
interface VaxAddInfoDAO {
    fun insertVaxInfo(vaxAddInfo: VaxAddInfoData): Boolean
    fun deleteVaxInfo(vaxId: Int): Boolean
    fun getVaxAddInfo(vaxId: Int): VaxAddInfoData
    fun getVaxId(vaxNameCompany: String): Int
    fun getAllVaxAddInfo(): Set<VaxAddInfoData>
}