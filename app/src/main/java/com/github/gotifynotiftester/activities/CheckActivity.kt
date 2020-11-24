package com.github.gotifynotiftester.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.gotifynotiftester.R
import com.github.gotify.connector.*
import com.github.gotifynotiftester.services.customServiceName

class CheckActivity : Activity() {

    private var url: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        val btn: Button = findViewById<View>(R.id.button_notify) as Button
        btn.isEnabled = false

        //bind to the service
        binding.bindRemoteService()
    }

    private val binding = GotifyServiceBinding(this, object : GotifyBindingHandler {
        override fun onConnected(service: GotifyServiceBinding) {
            findViewById<TextView>(R.id.text_result_can_bind).apply {
                text = "connected"
            }
            /**
             * We need to register the service where gotify has to send the notifications
             */
            service.registerApp("${packageName}${customServiceName}")
        }

        override fun onRegistered(service: GotifyServiceBinding, registration: Registration) {
            registerGotifyIdInSharedPref(service.context,registration.senderUid)
            token = registration.token
            url = registration.url
            findViewById<TextView>(R.id.text_result_register).apply {
                text = "true"
            }
            findViewById<TextView>(R.id.text_token_value).apply {
                text = token
            }
            findViewById<TextView>(R.id.text_url_value).apply {
                text = url
            }
            val btn: Button = findViewById<View>(R.id.button_notify) as Button
            btn.isEnabled = true
        }

        override fun onUnregistered(service: GotifyServiceBinding) {
            service.unbindRemoteService()
            val intent = Intent(applicationContext,
                    MainActivity::class.java)
            startActivity(intent)
        }
    })

    override fun onDestroy() {
        super.onDestroy()
        binding.unbindRemoteService()
    }

    fun unregister(view: View) {
        Toast.makeText(this, "Unregistering", Toast.LENGTH_SHORT).show()
        binding.unregisterApp()
    }

    fun sendNotification(view: View) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "$url/message?token=$token"
        val stringRequest: StringRequest =
            object :
                StringRequest(Method.POST, url, object : Response.Listener<String?>{
                    override fun onResponse(response: String?) {
                        Toast.makeText(applicationContext,"Done",Toast.LENGTH_SHORT).show()
                    }
                },
                    Response.ErrorListener { Toast.makeText(applicationContext,"An error occurred",Toast.LENGTH_SHORT).show() }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = mutableMapOf<String,String>()
                    params["title"] = "Test"
                    params["message"] = "From Gotify"
                    params["priority"] = "5"
                    return params
                }
            }
        requestQueue.add(stringRequest)
    }
}