package com.example.musickly

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInPage : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference

    companion object{
        const val KEY2 ="com.example.musickly.SignInPage.name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in_page)

        val signInButton = findViewById<Button>(R.id.signbtn)
        val username = findViewById<TextInputEditText>(R.id.usernamee)
        val password = findViewById<TextInputEditText>(R.id.pass)
        val checkbox = findViewById<CheckBox>(R.id.checkbtn)


        signInButton.setOnClickListener {
            val uniqueID = username.text.toString()
            val uniquePass = password.text.toString()

            if(uniqueID.isNotEmpty() && checkbox.isChecked && uniquePass.isNotEmpty())
            {

                readData(uniqueID,uniquePass)

            }
            else if(uniquePass.isEmpty() && uniqueID.isEmpty())
            {
                Toast.makeText(this,"Please enter username and password", Toast.LENGTH_SHORT).show()
            }
            else if(uniqueID.isEmpty())
            {
                Toast.makeText(this,"Please enter username ", Toast.LENGTH_SHORT).show()
            }
            else if(uniquePass.isEmpty())
            {
                Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                checkbox.buttonTintList = ColorStateList.valueOf(Color.RED)
                Toast.makeText(this, "Please accept T&C", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun readData(uniqueID: String, uniquePass: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val checkbox = findViewById<CheckBox>(R.id.checkbtn)

        val loadingDialog = ProgressDialog(this).apply {
            setMessage("Fetching user data...")
            setCancelable(true)
            show()
        }
        // Querying for the user with the given uniqueID
        databaseReference.child(uniqueID).get().addOnSuccessListener {
            clearFields()

            if (it.exists()) {
                // Get the password and compare with the entered password
                val storedPass = it.child("password").value.toString() // Assuming "password" is the key for password
                if (storedPass == uniquePass) {
                    val name = it.child("name").value
                    val intentwelcome = Intent(this, MainActivity::class.java)
                    intentwelcome.putExtra(KEY2, name.toString())
                    startActivity(intentwelcome)
                    checkbox.isChecked = false
                } else {
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not Found", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            loadingDialog.dismiss()
            Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
//        val username = findViewById<TextInputEditText>(R.id.usernamee)
        val password = findViewById<TextInputEditText>(R.id.pass)
//        username.text?.clear()
        password.text?.clear()
    }
}