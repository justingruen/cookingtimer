package com.fanestudios.cookingtimer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fanestudios.cookingtimer.R
import org.w3c.dom.Text


class SurveyActivity : AppCompatActivity() {

    lateinit var btnTimerActivity: Button
    lateinit var tvInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        btnTimerActivity = findViewById(R.id.btn_timerAct)
        tvInfo = findViewById(R.id.tv_info)


        btnTimerActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)   //Come back and replace with surveys someday
            startActivity(intent)
            finish()
        }


    }
}