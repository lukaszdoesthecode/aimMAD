package com.example.madness.connections.vaccinations

import com.example.tryingmybest.db.files.vaccinations.VaccinationsData

/**
 * Interface representing the possible queries for the Vaccinations entity
 */
interface VaccinationsDAO {
    fun insertVaccination(vax: VaccinationsData): Boolean
    fun deleteVaccination(vaxId: Int): Boolean
    fun getNumberOfDoses(vaxId: Int): Int
    fun getTimeBetweenDoses(vaxId: Int): Int
    fun updateNoOfDosesVaccination(vaxId: Int, noOfDoses: Int): Boolean
    fun updateTimeBetweenDosesVaccination(vaxId: Int, timeBetweenDoses: Int): Boolean
    fun getVaccination(vaxId: Int): VaccinationsData?
    fun getAllVaccinations(): Set<VaccinationsData?>?
    fun getAllVaccinationAndAddInfo(): Set<VaccinationsData?>?
    fun getVaxIdByName(): Int
}