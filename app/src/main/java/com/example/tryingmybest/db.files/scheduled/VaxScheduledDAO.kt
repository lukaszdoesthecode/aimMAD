package com.example.tryingmybest.db.files.scheduled

interface VaxScheduledDAO {
    fun insertVaxScheduled(vaxScheduled: VaxScheduledData): Boolean
}