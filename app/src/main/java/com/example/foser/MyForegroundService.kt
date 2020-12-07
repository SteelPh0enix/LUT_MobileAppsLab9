package com.example.foser

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.concurrent.timerTask

class MyForegroundService : Service() {
    companion object Constants {
        val CHANNEL_ID = "MyForegroundServiceChannel"
        val CHANNEL_NAME = "FoSer service channel"

        val MESSAGE = "message"
        val TIME = "time"
        val WORK = "work"
        val WORK_DOUBLE = "work_double"
    }

    var message: String = ""
    private var show_time: Boolean = true
    private var do_work: Boolean = false
    private var double_speed: Boolean = false

    private val period = 2000

    var ctx: Context? = null
    var notificationIntent: Intent? = null
    var pendingIntent: PendingIntent? = null

    var counter = 0
    var timer: Timer? = null
    var timerTask: TimerTask? = null
    private val handler = Handler()

    private val runnable = Runnable {
        val notification = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_my_icon)
                .setContentTitle(getString(R.string.ser_title))
                .setShowWhen(show_time)
                .setContentText("$message $counter")
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.circle))
                .setContentIntent(pendingIntent)
                .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    override fun onCreate() {
        ctx = this
        notificationIntent = Intent(ctx, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        counter = 0
        timer = Timer()
        timerTask = timerTask {
            counter++
            handler.post(runnable)
        }

        super.onCreate()
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        timer?.cancel()
        timer?.purge()
        timer = null

        super.onDestroy()
    }

    fun doWork() {
//        val info = "Start working...\n show_time = $show_time\n do_work = $do_work\n double_speed = $double_speed"
//        Toast.makeText(this, info, Toast.LENGTH_LONG).show()

        if (do_work) {
            val timerPeriod: Long = if (double_speed) period / 2L else period.toLong()
            timer?.schedule(timerTask, 0L, timerPeriod)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)

        message = intent?.getStringExtra(MESSAGE).toString()
        show_time = intent?.getBooleanExtra(TIME, false) == true
        do_work = intent?.getBooleanExtra(WORK, false) == true
        double_speed = intent?.getBooleanExtra(WORK_DOUBLE, false) == true

        createNotificationChannel()

        val notification = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_my_icon)
                .setContentTitle(getString(R.string.ser_title))
                .setShowWhen(show_time)
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.circle))
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification)
        doWork()

        return START_NOT_STICKY
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel =NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}