package com.github.gotifynotiftester.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Messenger
import com.github.gotify.connector.GotifyMessage
import com.github.gotify.connector.GotifyServiceHandler
import com.github.gotify.connector.getGotifyIdInSharedPref
import java.util.concurrent.ThreadLocalRandom

const val customServiceName = ".services.CustomNotif" // ! Starts with a dot

class CustomNotif : Service(){

    private var notifier: Notifier? = null

    private val notifMessenger = Messenger(MessageHandler(this))

    internal class MessageHandler(var service: CustomNotif) : GotifyServiceHandler(){
        override fun onMessage(message: GotifyMessage) {
            val text = message.message
            var title = message.title
            if (title == null) {
                title = service.applicationInfo.name
            }
            var priority = message.priority
            if (priority == null){
                priority = 8
            }
            service.notifier?.sendNotification(title!!,text!!,priority.toInt())
        }

        override fun isTrusted(uid: Int): Boolean {
            return uid == getGotifyIdInSharedPref(service)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return notifMessenger.binder
    }

    override fun onCreate() {
        notifier = Notifier(this)
        notifier?.init()
        super.onCreate()
    }
}

class Notifier(var context: Context){
    /** For showing and hiding our notification.  */
    private var gNM: NotificationManager? = null
    private var channelId = "gotifyChannelID"

    fun init() {
        channelId = context.packageName
        createNotificationChannel()
        gNM = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun sendNotification(title: String,text: String, priority: Int){
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        val notification =
                notificationBuilder.setSmallIcon(context.applicationInfo.icon) // the status icon
                        .setTicker(text) // the status text
                        .setWhen(System.currentTimeMillis()) // the time stamp
                        .setContentTitle(title) // the label of the entry
                        .setContentText(text) // the contents of the entry
                        .setPriority(priority)
                        .build()

        gNM!!.notify(ThreadLocalRandom.current().nextInt(), notification)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelId.isNotEmpty()) {
            val name = context.packageName
            val descriptionText = "gotify"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
