package com.example.foser

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    private var buttonStart: Button? = null
    private var buttonStop: Button? = null
    private var buttonRestart: Button? = null

    private var textInfoService: TextView? = null
    private var textInfoSettings: TextView? = null

    private var message = ""
    private var show_time = false
    private var work = false
    private var work_double = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStart = findViewById(R.id.buttonStart)
        buttonStop = findViewById(R.id.buttonStop)
        buttonRestart = findViewById(R.id.buttonRestart)

        textInfoService = findViewById(R.id.textInfoServiceState)
        textInfoSettings = findViewById(R.id.textInfoSettings)

        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.itemExit -> {
                finishAndRemoveTask()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clickStart(view: View) {
//        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
        getPreferences()

        val startIntent = Intent(this, MyForegroundService::class.java)
        startIntent.putExtra(MyForegroundService.MESSAGE, message)
        startIntent.putExtra(MyForegroundService.TIME, show_time)
        startIntent.putExtra(MyForegroundService.WORK, work)
        startIntent.putExtra(MyForegroundService.WORK_DOUBLE, work_double)

        ContextCompat.startForegroundService(this, startIntent)
        updateUI()
    }

    fun clickStop(view: View) {
//        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
        val stopIntent = Intent(this, MyForegroundService::class.java)
        stopService(stopIntent)
        updateUI()
    }

    fun clickRestart(view: View) {
        clickStop(view)
        clickStart(view)
    }

    private fun getPreferences(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        message = sharedPreferences.getString("message", "FoSer").toString()
        show_time = sharedPreferences.getBoolean("show_time", true)
        work = sharedPreferences.getBoolean("sync", true)
        work_double = sharedPreferences.getBoolean("double", false)

        return "Message: $message\nshow_time: $show_time\nwork: $work\ndouble: $work_double"
    }

    private fun updateUI() {
        if (isMyForegroundServiceRunning()) {
            buttonStart?.isEnabled = false
            buttonStop?.isEnabled = true
            buttonRestart?.isEnabled = true
            textInfoService?.text = getString(R.string.info_service_running)
        } else {
            buttonStart?.isEnabled = true
            buttonStop?.isEnabled = false
            buttonRestart?.isEnabled = false
            textInfoService?.text = getString(R.string.info_service_not_running)
        }
        textInfoSettings?.text = getPreferences()
    }

    @SuppressWarnings("deprecation")
    private fun isMyForegroundServiceRunning(): Boolean {
        val myServiceName = MyForegroundService::class.java.name
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (runningService in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            val runningServiceName = runningService.service.className
            if (runningServiceName == myServiceName) {
                return true
            }
        }
        return false
    }
}