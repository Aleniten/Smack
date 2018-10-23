package com.aleapp.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aleapp.smack.R
import com.aleapp.smack.Services.AuthService
import com.aleapp.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun createAvatarImageViewClicked(view:View){
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0){
            userAvatar = "light$avatar"
        } else{
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImageView.setImageResource(resourceId)
    }

    fun createBackgroundColorBtnClicked(view: View){
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserBtnClicked(view: View){
        Log.d("CreateUserActivity", "------> createUserBtnClicked()")
        enableSpinner(true)
        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(this,email,password){registerSuccess ->
                if (registerSuccess){
                    Log.d("CreateUserActivity", "registerSuccess")
                    AuthService.loginUser(this, email,password){loginSuccess ->
                        Log.d("CreateUserActivity", "loginUser")
                        if(loginSuccess){
                            AuthService.createUser(this, userName, email, userAvatar, avatarColor){createSuccess ->
                                Log.d("CreateUserActivity", "createUser")
                                if(createSuccess){

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Make sure name, email and password are fill in", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong try it again.", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            createSpinner.visibility = View.VISIBLE
        } else{
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImageView.isEnabled = !enable
        createBackgrounColorBtn.isEnabled = !enable
    }
}
