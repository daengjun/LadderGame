package com.jun.ladder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.centerm.myapplication.R


class InputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val etCount = findViewById<EditText>(R.id.etCount)
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            val n = etCount.text.toString().toIntOrNull()
            if (n == null || n !in 3..10) {
                toast(getString(R.string.err_people_range)); return@setOnClickListener
            }
            findViewById<EditText>(R.id.etCount).setText("")
            startActivity(Intent(this, LadderActivity::class.java).putExtra("cols", n))
        }
    }

    private fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}