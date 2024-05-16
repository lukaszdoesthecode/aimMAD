package com.example.tryingmybest

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madness.connections.queries.auth.SuspendedQueriesAuth
import com.example.tryingmybest.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

/**
 * LogIn activity gives user access to the application. It verifies the user
 * with Firebase Authentication.
 */
class LogIn : AppCompatActivity() {

    /** The binding object that holds references to views within the activity's layout. */
    private lateinit var binding: ActivityLoginBinding

    /** The instance of FirebaseAuth for user authentication. */
    private lateinit var firebaseAuth: FirebaseAuth

    private var email: EditText? = null
    private var password: EditText? = null

    /**
     * Called when the activity is starting.
     * @param savedInstanceState The bundle containing the activity's previously saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating the layout and initializing the binding object
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Setting up click listener for sign up text view
        val signUp: TextView = findViewById(R.id.SignUpTV)
        email = binding.emailEV
        password = binding.passwordEV

        signUp.setOnClickListener { goToSignUp() }

        binding.button.setOnClickListener {
            var role = ""
            lifecycleScope.launch {
                    role = register()
                }
            lifecycleScope.launch {
                if (role == "ADMIN") {
                    goToAdminHome()
                } else {
                    val userId = withContext(Dispatchers.IO) {
                        SuspendedQueriesAuth.getAuth(
                            email?.text.toString(),
                            password?.text.toString()
                        )
                    }
                    if (userId != -1) {
                        goToHome(userId)
                    }
                }
            }
        }
}

    /**
     * Validates user input for email and password fields.
     * @return true if email and password fields are not empty, false otherwise.
     */
    private fun validate(): Boolean {
        if (email?.text.toString().isEmpty() || password?.text.toString().isEmpty()) {
            Toast.makeText(this, "Please fill out all your information", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Attempts to log in the user with the provided email and password using FirebaseAuth.
     * Shows toast messages based on the login result.
     */
    private suspend fun register(): String {
        val emailText = email?.text.toString()
        val passwordText = password?.text.toString()

        return try {
            // Attempt to sign in with Firebase Auth
            val authResult = firebaseAuth.signInWithEmailAndPassword(emailText, passwordText).await()
            if (authResult.user != null) {
                // Attempt to get additional auth info using SuspendedQueriesAuth
                val auth = SuspendedQueriesAuth.getAuth(emailText, passwordText)
                if (auth != -1) {
                    Toast.makeText(this@LogIn, "Login succeeded", Toast.LENGTH_SHORT).show()
                    "Login succeeded"
                } else {
                    Toast.makeText(this@LogIn, "Login failed", Toast.LENGTH_SHORT).show()
                    "Login failed"
                }
            } else {
                Toast.makeText(this@LogIn, "Login failed", Toast.LENGTH_SHORT).show()
                "Login failed"
            }
        } catch (e: Exception) {
            Toast.makeText(this@LogIn, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            "Login failed: ${e.message}"
        }
    }

    private suspend fun <T> Task<T>.await(): T {
        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener {
                if (it.isSuccessful) {
                    cont.resume(it.result, null)
                } else {
                    cont.resumeWithException(it.exception ?: Exception("Unknown task exception"))
                }
            }
        }
    }


    /**
     * Changes activity to the SignUp activity.
     */
    private fun goToSignUp() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Changes activity to the Home activity.
     */
    private fun goToHome(userId: Int) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("user_id", userId)
        startActivity(intent)
        finish()
    }

    private fun goToAdminHome() {
        val intent = Intent(this, Vaccines::class.java)
        startActivity(intent)
        finish()
    }
}
