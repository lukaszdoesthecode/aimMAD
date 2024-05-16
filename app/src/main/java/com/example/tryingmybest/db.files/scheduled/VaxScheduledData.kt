package com.example.tryingmybest.db.files.scheduled

import com.example.tryingmybest.db.files.entities.VaxStatus
import java.sql.Date

data class VaxScheduledData(
    var vaxId: Int,
    var vaxUserId: Int,
    var vaxDateOfFirstDose: Date? = null,
    var vaxStatus: Enum<VaxStatus>? = null
)