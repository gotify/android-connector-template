package com.github.gotifynotiftester.activities


import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.gotifynotiftester.R
import com.github.gotifynotiftester.gotify.*

class CheckActivity : GotifyServiceBinding() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        /** First of all, we set serviceName to our custom notif
         * service name to register it
         */
        serviceName = "$packageName.services.CustomNotif"

        val btn: Button = findViewById<View>(R.id.button_notify) as Button
        btn.isEnabled = false

        //bind to the service
        doBindService()
    }

    //we overwrite the handler to add functions based on the purpose of the msg
    internal inner class subHandler : gHandler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                TYPE_CLIENT_STARTED -> connected()
                TYPE_REGISTERED_CLIENT -> registered()
            }
        }
    }

    // we override the mMessenger to use our handler
    override val gMessenger = Messenger(subHandler())

    private fun connected() {
        findViewById<TextView>(R.id.text_result_can_bind).apply {
            text = "connected"
        }
        // is gotify connected to the server ?
        doRegisterApp()
    }

    private fun registered() {
        findViewById<TextView>(R.id.text_result_register).apply {
            text = "true"
        }
        findViewById<TextView>(R.id.text_token_value).apply {
            text = TOKEN
        }
        findViewById<TextView>(R.id.text_url_value).apply {
            text = URL
        }
        val btn: Button = findViewById<View>(R.id.button_notify) as Button
        btn.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    fun unregister(view: View) {
        Toast.makeText(this, "unregister", Toast.LENGTH_SHORT).show()
        doUnregisterApp()
    }

    fun sendNotification(view: View) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "$URL/message?token=$TOKEN"
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