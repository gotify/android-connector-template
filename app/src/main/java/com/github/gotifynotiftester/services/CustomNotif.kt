package com.github.gotifynotiftester.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.github.gotify.connector.GotifyServiceHandler
import com.github.gotify.connector.getGotifyIdInSharedPref

val customServiceName = "services.CustomNotif"

class CustomNotif : Service(){
    /**
     * Here you can custom your notification
     * you need to override showNotification
     */

    private val gMessenger = Messenger(gHandler())

    internal inner class gHandler : GotifyServiceHandler(this){
        override fun onMessage(message: Message) {
            super.onMessage(message)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return gMessenger.binder
    }
    }
