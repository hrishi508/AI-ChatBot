package com.example.aichatbot.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aichatbot.chat.ChatScreen
import com.example.aichatbot.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class LoginActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val username by lazy {
        binding.editTextEmail.text.toString()
            .substringBefore("@")
            .replace(".", "")
    }
    private val reference by lazy {
        FirebaseDatabase.getInstance("https://ai-chatbot-648d7-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
    }
    private val checkUserDatabase by lazy { reference.orderByChild("username").equalTo(username) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEmail()
        setupClickListeners()
        setupSignInFormValidators()
    }

    private fun setEmail() {
        val email = intent.getStringExtra("email")
        if (email?.isNotEmpty() == true) binding.editTextEmail.setText(email)
    }

    private fun setupClickListeners() {
        binding.buttonSignIn.setOnClickListener { validateUser() }
    }

    private fun validateUser() {
        val password = binding.editTextPassword.text.toString()
        checkUsernameAndPassword(checkUserDatabase, password)
    }

    private fun checkUsernameAndPassword(checkUserDatabase: Query, password: String) {
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.textinputEmail.error = null
                    var loginAttempts =
                        snapshot.child(username).child("loginAttempts").getValue(Int::class.java)
                    loginAttempts = loginAttempts?.plus(1)
                    snapshot.ref.child(username).child("loginAttempts").setValue(loginAttempts)

                    val passwordDB =
                        snapshot.child(username).child("password").getValue(String::class.java)

                    if (passwordDB.equals(password)) {
                        clearFormFields()
                        Toast.makeText(
                            this@LoginActivity,
                            "Signed in successfully!",
                            Toast.LENGTH_LONG
                        ).show()

                        loginAttempts = 0
                        resetLoginAttempts(snapshot)
                        startChatActivity()
                    } else {
                        binding.textinputPassword.error = "Incorrect Password"
                        binding.editTextPassword.text = null
                        binding.editTextPassword.requestFocus()

                        if (loginAttempts != null) {
                            if (loginAttempts > 2) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "3 failed login attempts! Please try later",
                                    Toast.LENGTH_LONG
                                ).show()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    loginAttempts = 0
                                    resetLoginAttempts(snapshot)
                                }, 300000)

                                onBackPressed()
                            }
                        }
                    }


                } else {
                    binding.textinputEmail.error = "User does not exist!"
                    binding.editTextEmail.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun clearFormFields() {
        binding.editTextEmail.text = null
        binding.editTextPassword.text = null

        binding.editTextEmail.clearFocus()
        binding.editTextPassword.clearFocus()
    }

    private fun resetLoginAttempts(snapshot: DataSnapshot) {
        val tmp = 0
        snapshot.ref.child(username).child("loginAttempts").setValue(tmp)
    }

    private fun startChatActivity() {
        val intent = Intent(this, ChatScreen::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    private fun setupSignInFormValidators() {
        binding.editTextEmail.addTextChangedListener {
            val email = binding.editTextEmail.text.toString()
            if (email.isNotEmpty() && !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")))
                binding.textinputEmail.error = "Invalid Email!"
            else
                binding.textinputEmail.error = null
        }

        binding.editTextPassword.addTextChangedListener {
            val password = binding.editTextPassword.text.toString()
            if (password.isNotEmpty())
                binding.textinputPassword.error = null
        }
    }
}