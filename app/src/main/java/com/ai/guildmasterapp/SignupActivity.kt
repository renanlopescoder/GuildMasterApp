package com.ai.guildmasterapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var apiKeyInput: EditText
    private lateinit var signupButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        apiKeyInput = findViewById(R.id.apiKey)
        signupButton = findViewById(R.id.signupButton)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val apiKey = apiKeyInput.text.toString().trim()

            if (email.isEmpty()) {
                emailInput.error = "Email required"
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Password is required"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            if (apiKey.isEmpty()) {
                apiKeyInput.error = "API key is required, you can find on your Guild Wars 2 Account"
                apiKeyInput.requestFocus()
                return@setOnClickListener
            }

            createUser(email, password, apiKey)
        }
    }

    private fun createUser(email: String, password: String, apiKey: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, email, apiKey)
                    }
                } else {
                    Toast.makeText(this, "Signup failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, apiKey: String) {
        val user = hashMapOf(
            "email" to email,
            "apiKey" to apiKey,
        )

        // users collection
        firestore.collection("users")
            .document(uid) // Using uid as the document id
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
    }
}
