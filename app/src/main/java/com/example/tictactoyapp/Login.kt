package com.example.tictactoyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
    }

    fun buLoginEvent(view: View) {
        loginToFirebase(etMail.text.toString(), etPassword.text.toString())
    }

    fun loginToFirebase(email: String, password: String) {

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Successful Login", Toast.LENGTH_LONG).show()
                    var currentUser = mAuth!!.currentUser
                    if (currentUser != null) {

                        //save in database
                        myRef.child("Users").child(splitString(currentUser.email.toString())).child("Request").setValue(currentUser.uid)

                    }
                    loadMain()
                } else {
                    Toast.makeText(applicationContext, "Fail Login", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        loadMain()
    }

    fun loadMain() {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null) {

            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser!!.email)
            intent.putExtra("uid", currentUser!!.uid)
            startActivity(intent)

            finish()
        }
    }


    fun splitString(str: String): String {
        var split = str.split("@")
        return split[0]
    }
}




