package com.example.facechat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.concurrent.TimeUnit

class Registration : AppCompatActivity() {
     private var checker:String=""
    private var phoneNumber:String=""

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    lateinit var mVerificationId:String
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    //private var loadingBar=ProgressDialog.show(this,"Phone Number Verification","Please Wait,Verifying Phone Number")
    //private var verBar=ProgressDialog.show(this,"Code Verification","Please Wait,Verifying The Code")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        ccp.registerCarrierNumberEditText(phoneText)

        continueNextButton.setOnClickListener {
            if ((continueNextButton.text == "Submit")||(checker=="Code Sent"))
            {
                val verificationcode:String=codeText.text.toString()
                if (verificationcode=="")
                {
                    Toast.makeText(this,"Please Enter A Valid Code",Toast.LENGTH_SHORT).show()
                }
                else
                {
                   // verBar.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode)
                    signInWithPhoneAuthCredential(credential)
                }
            }
            else {
                phoneNumber = ccp.fullNumberWithPlus
                if (phoneNumber != "")
                {
                   // loadingBar.setCanceledOnTouchOutside(false)
                   // loadingBar.show()
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, callbacks)
                }
                else {
                    Toast.makeText(this, "Please Enter a valid Phone Number", Toast.LENGTH_SHORT).show()
                }
            }
            }

        callbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(this@Registration,"Invalid Phone Number",Toast.LENGTH_SHORT).show()
               // loadingBar.dismiss()
                phoneText.visibility= View.VISIBLE
                continueNextButton.text="Continue"
                codeText.visibility=View.GONE
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                mVerificationId=verificationId
                mResendToken=token

                phoneAuth.visibility=View.GONE
                checker="Code Sent"
                continueNextButton.text="Submit"
                codeText.visibility=View.VISIBLE
               // loadingBar.dismiss()
                Toast.makeText(this@Registration,"Code Sent",Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                  // loadingBar.dismiss()
                    Toast.makeText(this,"Successful LogIn",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                else
                {
                   // loadingBar.dismiss()
                    val e:String=task.exception.toString()
                    Toast.makeText(this, "Error$e",Toast.LENGTH_SHORT).show()
                }
            }
    }
}
