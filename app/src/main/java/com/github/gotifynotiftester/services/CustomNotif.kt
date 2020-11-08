package com.github.gotifynotiftester.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import com.github.gotify.connector.GotifyServiceHandler

val customServiceName = "services.CustomNotif"

class CustomNotif : Service(){
    /**
     * Here you can custom your notification
     * you need to override showNotification
     */

    private val gMessenger = Messenger(gHandler())

    internal inner class gHandler : GotifyServiceHandler(this){
        override fun onMessage(message: Message) {
            Toast.makeText(applicationContext, "You received a message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return gMessenger.binder
    }
}
