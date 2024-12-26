package com.example.musickly

import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpPage : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)
        val signupbtn = findViewById<Button>(R.id.btn)
        signupbtn.setOnClickListener {
            val name =findViewById<TextInputEditText>(R.id.name).text.toString()
            val username =findViewById<TextInputEditText>(R.id.username).text.toString()
            val password =findViewById<TextInputEditText>(R.id.password).text.toString()
            val email =findViewById<TextInputEditText>(R.id.email).text.toString()
            val check =findViewById<CheckBox>(R.id.checkbtn)

            if(name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() )
            {
                if(check.isChecked)
                {
                    val builder1 = AlertDialog.Builder(this)
                    builder1.setTitle("Are you sure")
                    builder1.setMessage("Do you want to Continue")
                    builder1.setIcon(R.drawable.baseline_double_arrow_24)
                    builder1.setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialogInterface, i ->

                        val user = User(username, name, password, email)
                        database = FirebaseDatabase.getInstance().getReference("Users")
                        database.child(username).setValue(user).addOnSuccessListener {
                            clearFields()
                            check.isChecked = false
                            Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }})
                    builder1.setNegativeButton("No",
                        DialogInterface.OnClickListener { dialogInterface, i ->  })
                    builder1.show()
                }
                else {
                    check.buttonTintList = ColorStateList.valueOf(Color.RED)
                    Toast.makeText(this, "Please accept T&C", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Enter all the details required", Toast.LENGTH_SHORT).show()
            }
            }

        val signIn = findViewById<TextView>(R.id.signbtn)
        signIn.setOnClickListener {
            val intent = Intent(this, SignInPage::class.java)
            startActivity(intent)
        }
        }

    private fun clearFields() {
        findViewById<TextInputEditText>(R.id.name).text?.clear()
        findViewById<TextInputEditText>(R.id.username).text?.clear()
        findViewById<TextInputEditText>(R.id.password).text?.clear()
        findViewById<TextInputEditText>(R.id.email).text?.clear()
    }
    }