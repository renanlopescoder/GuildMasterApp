package com.ai.guildmasterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import com.ai.guildmasterapp.MainActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty()) {
                Log.w("LoginActivity", "Email is empty")
                emailInput.error = "Email is required"
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Please provide a valid email"
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Password is required"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordInput.error = "Password should be at least 6 characters long"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginActivity", "Attempting to log in with email: $email")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                Log.i("LoginActivity", "Login successful for email: $email")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
