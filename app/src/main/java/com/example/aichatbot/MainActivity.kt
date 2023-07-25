package com.example.aichatbot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aichatbot.databinding.ActivityMainBinding
import com.example.aichatbot.verifyOTP.OTPVerificationDialog
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
            val result = authUsingOTP()
            if (result) register()
        }

        binding.buttonSignIn.setOnClickListener {
            if (signIn()) {
                // Create Intent to move to next activity
            } else {
                Toast.makeText(
                    this,
                    "Invalid credentials! Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun authUsingOTP(): Boolean {
        val sentOTP = sendOTP(binding.editTextEmail.text.toString())
        return showOTPDialog(sentOTP)
    }

    companion object {
        fun sendOTP(receiverEmail: String): String {
            while (true) {
                try {
                    val otp = generateOTP()
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

                    val thread = Thread(Runnable {
                        try {
                            Transport.send(mimeMessage)
                        } catch (e: MessagingException) {
                            e.printStackTrace()
                        }
                    })
                    thread.start()
                    return otp
                } catch (e: AddressException) {
                    e.printStackTrace()
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
            }
        }

        private fun generateOTP(): String {
            val randomPin = (Math.random() * 9000).toInt() + 1000
            return randomPin.toString()
        }
    }

    private fun showOTPDialog(sentOTP: String): Boolean {
        var result = false
        val otpVerificationDialog =
            OTPVerificationDialog(
                this,
                binding.editTextEmail.text.toString(),
                sentOTP
            )
        otpVerificationDialog.setCancelable(false)
        otpVerificationDialog.show()
        otpVerificationDialog.setDialogResult { result = it }
        return result
    }

    private fun register() {
        Toast.makeText(
            this,
            "Registration Successful! Please sign in to continue.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun signIn(): Boolean {
        return true
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