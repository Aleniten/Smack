package com.aleapp.smack.Services

import android.content.Context
import android.util.Log
import com.aleapp.smack.Utilities.URL_CREATE_USER
import com.aleapp.smack.Utilities.URL_LOGIN
import com.aleapp.smack.Utilities.URL_REGISTER
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.VolleyLog
import com.android.volley.VolleyError



object AuthService {
    val TAG = AuthService.javaClass.simpleName;
    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""


    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

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
        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, JSONObject(requestBody), Response.Listener { response ->

            try {
                Log.d(TAG,response.toString())
                userEmail = response.getString("user")
                authToken = response.getString("token")
                Log.d(TAG,authToken)
                isLoggedIn = true
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
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

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
                    headers.put("Authorization", "Bearer $authToken")
                    return headers
                }
//                override fun getHeaders(): MutableMap<String, String> {
//                    val headers = HashMap<String, String>()
//                    headers.put("Autorizathion", "Bearer $authToken")
//                    return headers
//                }
            }
        Volley.newRequestQueue(context).add(createRequest)
    }
}


