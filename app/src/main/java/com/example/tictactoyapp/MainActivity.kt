package com.example.tictactoyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    var myEmail:String?= null

    private var mFirebaseAnalytics:com.google.firebase.analytics.FirebaseAnalytics?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics= com.google.firebase.analytics.FirebaseAnalytics.getInstance(this)


        var b:Bundle=intent.extras!!
        myEmail=b.getString("email")
        IncommingCalls()
    }


  protected  fun buClick(view: View) {
        val buSelected = view as Button
        var cellId = 0
        when (buSelected.id) {
            R.id.bu1 -> cellId = 1
            R.id.bu2 -> cellId = 2
            R.id.bu3 -> cellId = 3
            R.id.bu4 -> cellId = 4
            R.id.bu5 -> cellId = 5
            R.id.bu6 -> cellId = 6
            R.id.bu7 -> cellId = 7
            R.id.bu8 -> cellId = 8
            R.id.bu9 -> cellId = 9
        }


     myRef.child("PlayerOnline").child(sessionID!!).child(cellId.toString()).setValue(myEmail)

    }
    var activePlayer = 1
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    fun playGame(cellId: Int, buSelected: Button) {
        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellId)
            activePlayer = 2


        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.darkGreen)
            player2.add(cellId)
            activePlayer = 1
        }
        buSelected.isEnabled = false
        checkWinner()
    }


    fun checkWinner() {

        var winer = -1

        // row 1, 2, 3
        if ((player1.contains(1) && player1.contains(2) && player1.contains(3)) || (player1.contains(4) && player1.contains(5) && player1.contains(6)) || (player1.contains(7) && player1.contains(8) && player1.contains(9))) {
            winer = 1
        }
        if ((player2.contains(1) && player2.contains(2) && player2.contains(3)) || (player2.contains(4) && player2.contains(5) && player2.contains(6)) || (player2.contains(7) && player2.contains(8) && player2.contains(9))) {
            winer = 2
        }


        // col 1,2,3
        if ((player1.contains(1) && player1.contains(4) && player1.contains(7)) || (player1.contains(2) && player1.contains(5) && player1.contains(8)) || (player1.contains(3) && player1.contains(6) && player1.contains(9))){
            winer = 1
        }
        if ((player2.contains(1) && player2.contains(4) && player2.contains(7)) || (player2.contains(2) && player2.contains(5) && player2.contains(8))  ||(player2.contains(3) && player2.contains(6) && player2.contains(9))) {
            winer = 2
        }

        //cross1,2
        if ((player1.contains(1) && player1.contains(5) && player1.contains(9)) || (player1.contains(3) && player1.contains(5) && player1.contains(7)))  {
            winer = 1
        }
        if ((player2.contains(1) && player2.contains(5) && player2.contains(9)) || (player2.contains(3) && player2.contains(5) && player2.contains(7))) {
            winer = 2
        }

        if (winer != -1) {
            Toast.makeText(this, "Player 1 winner the game", Toast.LENGTH_LONG).show()

        } else{
            Toast.makeText(this, "Player 2 winner the game", Toast.LENGTH_LONG).show()
            }


    }

    fun autoPlay(cellId: Int) {

        val r = Random()
        val randIndex = r.nextInt(emptyCells.size)
        val cellId = emptyCells[randIndex]

        var buSelected: Button?
        buSelected = when (cellId) {
            1 -> bu1
            2 -> bu2
            3 -> bu3
            4 -> bu4
            5 -> bu5
            6 -> bu6
            7 -> bu7
            8 -> bu8
            9 -> bu9
            else -> {
                bu1
            }

        }

        playGame(cellId, buSelected)

    }
    fun buRequestEvent(view:android.view.View){
        var userDemail=etEmail.text.toString()
        myRef.child("Users").child(splitString(userDemail)).child("Request").push().setValue(myEmail)
        playerOnline(splitString(myEmail!!) + splitString(userDemail))
        playerSymbol= "x"

    }

    fun buAcceptEvent(view:android.view.View){
        var userDemail = etEmail.text.toString()
        myRef.child("Users").child(splitString(userDemail)).child("Request").push().setValue(myEmail)
        playerOnline(splitString(userDemail) + splitString(myEmail!!))
        playerSymbol= "o"
    }

    var sessionID:String?=null
    var playerSymbol:String?=null
    fun playerOnline(sessionID:String){
        this.sessionID = sessionID

        myRef.child("playerOnline").child(sessionID)
    }

    fun IncommingCalls(){
        myRef.child("Users").child(splitString(myEmail!!)).child("Request")
            .addValueEventListener(object: ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        val td=p0!!.value as HashMap<String,Any>
                        if (td!=null){
                            var value:String
                            for (key in td.keys){
                                value = td[key] as String
                                etEmail.setText(value)
                                myRef.child("Users").child(splitString(myEmail!!)).child("Request").setValue(true)

                                break
                            }
                        }

                    }catch (ex:Exception){
                        ex.printStackTrace()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun splitString(str: String): String {
        var split = str.split("@")
        return split[0]
    }


}