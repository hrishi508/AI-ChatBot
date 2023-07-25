package com.example.aichatbot

import android.provider.ContactsContract.CommonDataKinds.Email
import java.io.Serializable

data class User (val name: String, val email: Email, val password: String) : Serializable {
}