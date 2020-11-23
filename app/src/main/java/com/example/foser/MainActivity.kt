package com.example.foser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
    }

    fun clickStop(view: View) {
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show()
    }

    fun clickRestart(view: View) {
        clickStop(view)
        clickStart(view)
    }
}