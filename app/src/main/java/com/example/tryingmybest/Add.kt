package com.example.tryingmybest

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.example.madness.connections.queries.vacxaddinfo.SuspendedQueriesVaxAddInfo
import com.example.madness.connections.vaccinations.SuspendedQueriesVaccinations
import com.example.tryingmybest.db.files.entities.VaxStatus
import com.example.tryingmybest.db.files.scheduled.SuspendedQueriesVaxScheduled
import com.example.tryingmybest.db.files.scheduled.VaxScheduledData
import com.example.tryingmybest.db.files.vacxaddinfo.VaxAddInfoData
import com.example.tryingmybest.notifications.Notifications
import com.example.tryingmybest.notifications.Notify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Add : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var name: String
    private lateinit var dropdownMenu: Spinner
    private var isVaccineChosen = false
    private lateinit var saveButton: Button
    private lateinit var date: TextView
    private lateinit var addNotifTV: TextView
    private lateinit var setAlarmLayout: View
    private lateinit var showFormat: SimpleDateFormat
    private lateinit var sendDateFormat: SimpleDateFormat
    private lateinit var fullFormat: SimpleDateFormat
    private lateinit var fullSendFormat: SimpleDateFormat
    private var sendDate: String = ""
    private var sendNoti: String = ""
    private var lastDate: String = ""
    private var desc: String = ""
    private var doses: Long = 0
    private var duration: Long = 0
    private lateinit var email: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add, container, false)

        val user = FirebaseAuth.getInstance().currentUser
        email = user?.email.toString()

        dropdownMenu = view.findViewById(R.id.dropdownMenu)
        date = view.findViewById(R.id.date)
        addNotifTV = view.findViewById(R.id.add_notif)
        setAlarmLayout = view.findViewById(R.id.SetAlarm)

        fullSendFormat = SimpleDateFormat("dd-MM-yyyy, hh:mm", Locale.getDefault())
        fullFormat = SimpleDateFormat("EEE, MMM dd, yyyy hh:mm", Locale.getDefault())
        showFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
        sendDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        date.text = "Set appointment date"
        addNotifTV.text = "Set notification date"

        val intent = requireActivity().intent
        val userId = intent.getIntExtra("user_id", 0)

        setupDropdownMenu(userId)

        date.setOnClickListener {
            showDatePicker()
        }

        setAlarmLayout.setOnClickListener {
            val alertDialog = Notifications()
            alertDialog.show(parentFragmentManager, "NotificationsDialogFragment")
        }

        saveButton = view.findViewById(R.id.save)
        saveButton.setOnClickListener {
            saveToFireStore()
            scheduleNotification(sendNoti)
            Log.d(TAG, "Save button clicked.")

            lifecycleScope.launch {
                try {
                    val dropdownResult = dropdown(userId)
                    Log.e(TAG, "Dropdown result: $dropdownResult")
                    if (dropdownResult != null) {
                        val (vaxId, userId, date) = dropdownResult
                        Log.d(TAG, "Dropdown result: vaxId=$vaxId, userId=$userId, date=$date")
                        val vaxScheduledData =
                            VaxScheduledData(vaxId, userId, date, VaxStatus.TO_DO)
                        val insertResult =
                            SuspendedQueriesVaxScheduled.insertVaxScheduled(vaxScheduledData)
                    } else{
                        Log.e(TAG, "Error getting dropdown result")
                    }

                    }catch (e: CancellationException) {
                        Log.e(TAG, "Coroutine cancelled", e)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in coroutine", e)
                    }
            }}



        return view
    }

    private fun setupDropdownMenu(userId: Int) {
        lifecycleScope.launch {
            val addVaxInfo = SuspendedQueriesVaxAddInfo.getAllVaxAddInfo()

            val options = mutableListOf<String>()
            val vaccineMap = mutableMapOf<String, VaxAddInfoData>()
            options.add("Select Vaccine")
            for (document in addVaxInfo) {
                val optionName = document.vaxNameCompany
                optionName?.let {
                    options.add(it)
                    vaccineMap[it] = document
                }
            }
            val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, options)
            adapter.setDropDownViewResource(R.layout.item_dropdown)
            dropdownMenu.adapter = adapter

            dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SimpleDateFormat")
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    isVaccineChosen = position != 0
                    enableSaveButton()
                    name = dropdownMenu.selectedItem.toString()
                    val selectedDocument = vaccineMap[name]
                    if (selectedDocument != null) {
                        lifecycleScope.launch {
                            val vaxId = SuspendedQueriesVaxAddInfo.getVaxId(name)
                            val noOfDoses = SuspendedQueriesVaccinations.getNumberOfDoses(vaxId)
                            val timeBetweenDoses = SuspendedQueriesVaccinations.getTimeBetweenDoses(vaxId)

                            doses = noOfDoses.toLong()
                            duration = timeBetweenDoses.toLong()

                            var latestNextDose: Date? = null
                            var latestAppointmentDocument: DocumentSnapshot? = null

                            db.collection("appointments")
                                .whereEqualTo("name", name)
                                .get()
                                .addOnCompleteListener { appointmentDocuments ->
                                    if (appointmentDocuments.isSuccessful) {
                                        for (appointmentDocument in appointmentDocuments.result!!) {
                                            val nextDose = appointmentDocument.getDate("nextDose")
                                            if (nextDose != null && (latestNextDose == null || nextDose.after(latestNextDose))) {
                                                latestNextDose = nextDose
                                                latestAppointmentDocument = appointmentDocument
                                                sendDate = nextDose.toString()
                                            }
                                        }

                                        if (latestAppointmentDocument != null) {
                                            lastDate = sendDateFormat.format(latestNextDose!!)
                                            sendDate = sendDateFormat.format(latestNextDose!!)
                                            updateProposedDate()
                                        }

                                        updateProposedDate()
                                    } else {
                                        Log.e(TAG, "Error getting documents: ${appointmentDocuments.exception}")
                                    }
                                }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected
                }
            }
        }
    }

    private suspend fun dropdown(userId: Int): Triple<Int, Int, java.sql.Date>? {
        return suspendCancellableCoroutine { continuation ->
            dropdownMenu.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SimpleDateFormat")
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        isVaccineChosen = true
                        enableSaveButton()
                        name = dropdownMenu.selectedItem.toString()
                        lifecycleScope.launch {
                            val vaxId = SuspendedQueriesVaxAddInfo.getVaxId(name)
                            val noOfDoses = SuspendedQueriesVaccinations.getNumberOfDoses(vaxId)
                            val timeBetweenDoses = SuspendedQueriesVaccinations.getTimeBetweenDoses(vaxId)

                            doses = noOfDoses.toLong()
                            duration = timeBetweenDoses.toLong()

                            var latestNextDose: Date? = null
                            var latestAppointmentDocument: DocumentSnapshot? = null

                            db.collection("appointments")
                                .whereEqualTo("name", name)
                                .get()
                                .addOnCompleteListener { appointmentDocuments ->
                                    if (appointmentDocuments.isSuccessful) {
                                        for (appointmentDocument in appointmentDocuments.result!!) {
                                            val nextDose = appointmentDocument.getDate("nextDose")
                                            if (nextDose != null && (latestNextDose == null || nextDose.after(latestNextDose))) {
                                                latestNextDose = nextDose
                                                latestAppointmentDocument = appointmentDocument
                                                sendDate = nextDose.toString()
                                            }
                                        }

                                        if (latestAppointmentDocument != null) {
                                            lastDate = sendDateFormat.format(latestNextDose!!)
                                            sendDate = sendDateFormat.format(latestNextDose!!)
                                            updateProposedDate()
                                        }

                                        updateProposedDate()
                                        if (!continuation.isCompleted) {
                                            continuation.resume(Triple(vaxId, userId, java.sql.Date(sendDateFormat.parse(sendDate)!!.time))) {
                                                // Handle cancellation if needed
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, "Error getting documents: ${appointmentDocuments.exception}")
                                        if (!continuation.isCompleted) {
                                            continuation.resume(null) {
                                                // Handle cancellation if needed
                                            }
                                        }
                                    }
                                }
                        }
                    } else {
                        if (!continuation.isCompleted) {
                            continuation.resume(null) {
                                // Handle cancellation if needed
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (!continuation.isCompleted) {
                        continuation.resume(null) {
                            // Handle cancellation if needed
                        }
                    }
                }
            }
        }
    }

    private fun updateProposedDate() {
        val calendar = Calendar.getInstance()

        if (sendDate.isEmpty()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        } else {
            try {
                calendar.time = sendDateFormat.parse(sendDate) ?: Date()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            calendar.add(Calendar.DAY_OF_YEAR, duration.toInt())
        }

        val proposedDate = showFormat.format(calendar.time)
        date.text = proposedDate
        sendDate = sendDateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val dayBefore = fullFormat.format(calendar.time)
        addNotifTV.text = dayBefore
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                val proposedDate = showFormat.format(calendar.time)
                date.text = proposedDate

                sendDate = sendDateFormat.format(calendar.time)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun enableSaveButton() {
        if (isVaccineChosen) {
            context?.let {
                saveButton.setBackgroundColor(
                    ContextCompat.getColor(
                        it,
                        R.color.green
                    )
                )
            }
            saveButton.isEnabled = true
        } else {
            saveButton.isEnabled = false
        }
    }

    private fun saveToFireStore() {
        val appointmentData: HashMap<String, Any> = if (lastDate.isNotEmpty()) {
            hashMapOf(
                "name" to name,
                "email" to email,
                "nextDose" to sendDateFormat.parse(sendDate)!!,
                "lastDose" to sendDateFormat.parse(lastDate)!!,
                "desc" to desc
            )
        } else {
            hashMapOf(
                "name" to name,
                "email" to email,
                "nextDose" to sendDateFormat.parse(sendDate)!!,
                "desc" to desc
            )
        }

        db.collection("appointments").add(appointmentData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val vaccineRef = db.collection("vaccinations").whereEqualTo("name", name)

                    vaccineRef.get().addOnSuccessListener { querySnapshot ->
                        db.runTransaction { transaction ->
                            for (document in querySnapshot.documents) {
                                val currentDoses = document.getLong("doses") ?: 0
                                val newDoses = currentDoses - 1
                                transaction.update(document.reference, "doses", newDoses)
                            }
                        }.addOnSuccessListener {
                            Log.d(TAG, "Transaction successfully committed.")
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Transaction failed: ", e)
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error getting documents: ", e)
                    }

                } else {
                    Log.e(TAG, "Error saving appointment data: ", task.exception)
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error saving appointment data: ", exception)
            }

        val notificationData = hashMapOf(
            "name" to name,
            "date" to sendNoti,
            "email" to email
        )

        db.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { _ ->
                // Handle failure
            }
    }

    @SuppressLint("SetTextI18n")
    fun setSelectedDateTime(selectedDate: String, selectedTime: String) {
        val originalFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault())
        sendNoti = "$selectedDate, $selectedTime"
        try {
            val dateObj = originalFormat.parse(selectedDate)
            val formattedDate = displayFormat.format(dateObj ?: Date())
            addNotifTV.text = "$formattedDate, $selectedTime"
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun scheduleNotification(selectedDateTime: String) {
        val intentNot = Intent(requireContext(), Notify::class.java)
        intentNot.putExtra("vaccinations", name)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intentNot,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        val formatter = SimpleDateFormat("dd-MM-yyyy, hh:mm", Locale.getDefault())
        val selectedDateTimeMillis = try {
            formatter.parse(selectedDateTime)?.time ?: return
        } catch (e: ParseException) {
            e.printStackTrace()
            return
        }

        calendar.timeInMillis = selectedDateTimeMillis

        val delayMillis = selectedDateTimeMillis - System.currentTimeMillis()
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent
        )
    }
}
