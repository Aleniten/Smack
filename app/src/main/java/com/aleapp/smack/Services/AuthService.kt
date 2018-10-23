package com.aleapp.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.aleapp.smack.Controller.App
import com.aleapp.smack.Utilities.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.VolleyLog
import com.android.volley.VolleyError
import java.lang.Error


object AuthService {
    val TAG = AuthService.javaClass.simpleName;
    // var isLoggedIn = false
    // var userEmail = ""
    // var authToken = ""


    fun registerUser( email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()
        Log.d(TAG,requestBody);
        Log.d(TAG, URL_REGISTER)


        val registerRequest = object : JsonObjectRequest(Method.POST, URL_REGISTER, JSONObject(requestBody), Response.Listener { response ->

            try {
                Log.d(TAG, response.toString());
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not registerUser user: $error")
            complete(false)
        }) {
//            override fun getBodyContentType(): String {
//                return "aplication/json; charset=utf-8"
//            }

//            override fun getBody(): ByteArray {
//                return requestBody.toByteArray()
//            }
        }

//        val  registerRequest = JsonObjectRequest(URL_REGISTER, JSONObject(requestBody),
//        Response.Listener<JSONObject> {
//
//            try {
//                //Process os success response
//            } catch (e: JSONException) {
//                Log.d(TAG, e.message);
//
//                e.printStackTrace();
//            }
//        }, object:Response.ErrorListener {
//
//            override fun onErrorResponse(error: VolleyError) {
//                VolleyLog.e("Error: ", error.message)
//            }
//        })

//        val registerRequest = object : StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener { response ->
//            println(response)
//            complete(true)
//        }, Response.ErrorListener { error ->
//            Log.d("ERROR", "Could not register user: $error")
//            complete(false)
//        }) {
//            override fun getBodyContentType(): String {
//                return "aplication/json; charset=utf-8"
//            }
//
//            override fun getBody(): ByteArray {
//                Log.d(TAG, requestBody);
//                return requestBody.toByteArray()
//            }
//        }
        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser( email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, JSONObject(requestBody), Response.Listener { response ->

            try {
                Log.d(TAG,response.toString())
                App.prefs.userEmail= response.getString("user")
                App.prefs.authToken = response.getString("token")
                Log.d(TAG,App.prefs.authToken)
                App.prefs.isLoogedIn = true
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
            complete(false)
        }) {
//            override fun getBodyContentType(): String {
//                return "aplication/json; charset=utf-8"
//            }
//
//            override fun getBody(): ByteArray {
//                return requestBody.toByteArray()
//            }
        }
        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser( name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()


        val createRequest =
            object : JsonObjectRequest(Method.POST, URL_CREATE_USER, JSONObject(requestBody), Response.Listener { response ->

                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC " + e.localizedMessage)
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not add user: $error")
                complete(false)
            }) {
                //                override fun getBodyContentType(): String {
//                    return "aplication/json; charset=utf-8"
//                }
//
//                override fun getBody(): ByteArray {
//                    return requestBody.toByteArray()
//                }
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                    return headers
                }
//                override fun getHeaders(): MutableMap<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers.put("Autorizathion", "Bearer $authToken")
//                    return headers
//                }
            }
        App.prefs.requestQueue.add(createRequest)
    }

    fun finUserByEmail(context: Context, complete: (Boolean) -> Unit){
        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.prefs.userEmail}", JSONObject(), Response.Listener {response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)

            }catch (e: JSONException){
                Log.d("JSON", "EXC: "+ e.localizedMessage)
            }
        }, Response.ErrorListener {error ->
            Log.d("Error", "User not Found: $error")
            complete(false)
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(findUserRequest)
    }
}


