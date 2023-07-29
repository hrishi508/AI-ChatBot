package com.example.aichatbot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aichatbot.databinding.ActivityMainBinding
import com.example.aichatbot.login.LoginActivity
import com.example.aichatbot.verifyOTP.OTPVerificationDialog
import com.google.firebase.database.FirebaseDatabase
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupClickListeners()
        setupRegistrationFormValidators()
    }

    private fun setupClickListeners() {
        binding.buttonRegister.setOnClickListener {
            authUsingOTP()
        }
        binding.buttonSignIn.setOnClickListener { startSignInActivity(null) }
    }

    private fun authUsingOTP() {
        val otp = generateOTP()
        showOTPDialog(otp)
        sendOTP(otp, binding.editTextEmail.text.toString())
    }

    companion object {
        fun sendOTP(otp: String, receiverEmail: String) {
            val NANOSEC_PER_SEC: Long = 1000*1000*1000
            val startTime = System.nanoTime()

            while ((System.nanoTime()-startTime) < 1*60*NANOSEC_PER_SEC) {
                try {
                    val senderEmail = "k.gunjan2010@gmail.com"
                    val senderPass = "zwmrrsyklwhrksja"

                    val properties = System.getProperties().apply {
                        put("mail.smtp.host", "smtp.gmail.com")
                        put("mail.smtp.port", "465")
                        put("mail.smtp.ssl.enable", "true")
                        put("mail.smtp.auth", "true")
                    }

                    val session = Session.getInstance(properties, object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(senderEmail, senderPass)
                        }
                    })

                    val mimeMessage = MimeMessage(session)
                    mimeMessage.addRecipient(
                        Message.RecipientType.TO,
                        InternetAddress(receiverEmail)
                    )
                    mimeMessage.subject = "Subject: OTP for Email Verification"
                    mimeMessage.setText(
                        """
                        Hi,                
                        The OTP for your email verification with AI-ChatBOT is $otp.
                        
                        Use this code to verify your email address entered by you while registering with us.
                        
                        Kindly ignore if this wasn't you.
                        
                        Team AI-ChatBOT
                    """.trimIndent()
                    )

                    val thread = Thread {
                        try {
                            Transport.send(mimeMessage)
                        } catch (e: MessagingException) {
                            e.printStackTrace()
                        }
                    }
                    thread.start()
                    return
                } catch (e: AddressException) {
                    e.printStackTrace()
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
        }

        fun generateOTP(): String {
            val randomPin = (Math.random() * 9000).toInt() + 1000
            return randomPin.toString()
        }
    }

    private fun showOTPDialog(sentOTP: String) {
        val otpVerificationDialog =
            OTPVerificationDialog(
                this,
                binding.editTextEmail.text.toString(),
                sentOTP
            )

        otpVerificationDialog.setDialogResult {
            if (it) {
                register()
                startSignInActivity(binding.editTextEmail.text.toString())
                clearFormFields()
            }
        }
        otpVerificationDialog.setCancelable(false)
        otpVerificationDialog.show()
    }

    private fun register() {
        val user = User(
            binding.editTextName.text.toString(),
            binding.editTextEmail.text.toString(),
            binding.editTextPassword.text.toString(),
            0
        )
        val username = binding.editTextEmail.text.toString().substringBefore("@").replace(".", "")
        val database =
            FirebaseDatabase.getInstance("https://ai-chatbot-648d7-default-rtdb.asia-southeast1.firebasedatabase.app")
        val myRef = database.getReference("users")

        myRef.child(username).setValue(user)
        myRef.child(username).child("username").setValue(username)

        Toast.makeText(
            this,
            "Registration Successful!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun startSignInActivity(email: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun clearFormFields() {
        binding.editTextName.text = null
        binding.editTextEmail.text = null
        binding.editTextPassword.text = null

        binding.editTextName.clearFocus()
        binding.editTextEmail.clearFocus()
        binding.editTextPassword.clearFocus()
    }

    private fun setupRegistrationFormValidators() {
        binding.passwordInputMeter.setEditText(binding.editTextPassword)
        binding.passwordInputMeter.setShowStrengthIndicator(false)
        binding.passwordInputMeter.setShowStrengthLabel(false)

        binding.editTextEmail.addTextChangedListener {
            val email = binding.editTextEmail.text.toString()
            if (email.isNotEmpty() && !email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) binding.textinputEmail.error =
                "Invalid Email!"
            else binding.textinputEmail.error = null
        }

        binding.editTextPassword.addTextChangedListener {
            if (it?.isEmpty() == true) {
                binding.passwordInputMeter.setShowStrengthIndicator(false)
                binding.passwordInputMeter.setShowStrengthLabel(false)
            } else {
                binding.passwordInputMeter.setShowStrengthIndicator(true)
                binding.passwordInputMeter.setShowStrengthLabel(true)
            }
        }
    }
}