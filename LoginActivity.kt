package com.rsd96.drive

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by Ramshad on 11/9/17.
 */
class LoginActivity: AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)

        btn_login_signup.setOnClickListener({view ->
            startActivity(Intent(this, SignUpActivity::class.java))
        })

        btn_login_reset_password.setOnClickListener({view ->
            startActivity(Intent(this, ResetPassActivity::class.java))
        })

        btn_login.setOnClickListener({view ->
            val email = et_email.getText().toString().trim()
            val password = et_password.getText().toString().trim()

            if (TextUtils.isEmpty(email))
                Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            else if (TextUtils.isEmpty(password))
                Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            else {
                progressBar.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            progressBar.visibility = View.GONE
                            if (!task.isSuccessful) {
                                // error
                                if (password.length <= 6) {
                                    et_password.setError(getString(R.string.minimum_password))
                                } else {
                                    Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
            }
        })

    }

}