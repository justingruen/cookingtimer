package com.fanestudios.cookingtimer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fanestudios.cookingtimer.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//Delete Current
//On Phys device, distancing between buttons is off


class MainActivity : AppCompatActivity() {

    //List of previously inputted values for each one? then I can use the reset button to setTimer so it doesnt go to 0 and instead to whatever it was
    //Change the inputted value for that one only when actually clicking the set timer button

    val startTimeInMillis: Long = 0

    lateinit var tvCountDownC: TextView
    lateinit var tvCountDownL: TextView
    lateinit var tvCountDownR: TextView
    lateinit var tvCounterC: TextView
    lateinit var tvCounterL: TextView
    lateinit var tvCounterR: TextView
    lateinit var btnSurvey: Button
    lateinit var btnStartPause: Button
    lateinit var btnReset: Button
    lateinit var btnDeleteCur: Button
    lateinit var btnResetTotal: Button
    lateinit var btnSetTimer: Button
    lateinit var btnAddInterval: Button
    lateinit var btnLeftArrow: Button
    lateinit var btnRightArrow: Button
    lateinit var pickerML: NumberPicker
    lateinit var pickerMR: NumberPicker
    lateinit var pickerSL: NumberPicker
    lateinit var pickerSR: NumberPicker

    lateinit var cdTimer: CountDownTimer

    var running: Boolean = false
    var setNew: Boolean = false
    var moveDir: Boolean = false
    var movingNext: Boolean = false

    var timeLeftInMillis: Long = 0
    var timeLeftInMillisL: Long = startTimeInMillis
    var timeLeftInMillisR: Long = 0

    var intervals: MutableList<Long> = mutableListOf(0)
    var currentCounter: Int = 1 //counters will be +1

    var minutes: Int = 0
    var seconds: Int = 0
    var timeLeftFormatted: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        pickerML = findViewById<NumberPicker>(R.id.np_minL)
        pickerMR = findViewById<NumberPicker>(R.id.np_minR)
        pickerSL = findViewById<NumberPicker>(R.id.np_secL)
        pickerSR = findViewById<NumberPicker>(R.id.np_secR)
        val pickers: List<NumberPicker> = listOf(pickerML, pickerMR, pickerSL, pickerSR)
        setVals(pickers)



        tvCountDownC = findViewById(R.id.tv_timerM)
        tvCountDownL = findViewById(R.id.tv_timerL)
        tvCountDownR = findViewById(R.id.tv_timerR)

        tvCounterC = findViewById(R.id.tv_counterM)
        tvCounterL = findViewById(R.id.tv_counterL)
        tvCounterR = findViewById(R.id.tv_counterR)

        tvCountDownL.visibility = View.INVISIBLE
        tvCountDownR.visibility = View.INVISIBLE
        tvCounterL.visibility = View.INVISIBLE
        tvCounterR.visibility = View.INVISIBLE

        btnSurvey = findViewById(R.id.btn_surveyAct)
        btnStartPause = findViewById(R.id.btn_start)
        btnReset = findViewById(R.id.btn_resetCur)
        btnDeleteCur = findViewById(R.id.btn_delete)
        btnResetTotal = findViewById(R.id.btn_resetAll)
        btnSetTimer = findViewById(R.id.btn_setTime)
        btnAddInterval = findViewById(R.id.btn_addInterval)
        btnLeftArrow = findViewById(R.id.btn_Left)
        btnRightArrow = findViewById(R.id.btn_Right)




        btnStartPause.setOnClickListener {
            if (running) {
                pauseTimer()
            }
//            if(timeLeftInMillis == 0.toLong()){       //Reset time if pressing start when at 0... doesn't work :(
//                resetTimer()
//            }
            else{       //Set it so if all are at 0, ask them to insert a time first?
                startTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }

        btnDeleteCur.setOnClickListener {
            deleteTimer()
        }

        btnResetTotal.setOnClickListener {
            resetAll()
        }

        btnSetTimer.setOnClickListener {
            setTimer()
        }

        btnAddInterval.setOnClickListener {
            addInterval()
        }

        btnLeftArrow.setOnClickListener {
            moveLeft()
        }

        btnRightArrow.setOnClickListener {
            moveRight()
        }

        btnSurvey.setOnClickListener {
            if(running){
                Toast.makeText(this, "Please pause the timer first", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, SurveyActivity::class.java)   //Come back and replace with surveys someday
                startActivity(intent)
                finish()
            }
        }

        updateCountDownText()

    }

    fun startTimer() {
        cdTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                if (currentCounter == intervals.size){
                    running = false
                    setNew = true
                    updateCountDownText()
                    btnStartPause.text = "Start"
                } else {
                    movingNext = true
                    moveLeft()
                    println("hello")
                    startTimer()
                }
            }
        }.start()

        running = true
        btnStartPause.text = "pause"
    }

    fun pauseTimer() {
        cdTimer.cancel()
        running = false
        btnStartPause.text = "Start"
    }

    fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        intervals[currentCounter - 1] = 0
    }

    fun resetAll(){
        setNew = true
        moveDir = true
        currentCounter = 2
        intervals = mutableListOf(0, 0, 0)
        updateCountDownText()

        currentCounter = 1
        tvCounterC.text = currentCounter.toString()
        tvCounterR.text = (currentCounter + 1).toString()
        intervals = mutableListOf(0)
        tvCountDownL.visibility = View.INVISIBLE
        tvCountDownR.visibility = View.INVISIBLE
        tvCounterL.visibility = View.INVISIBLE
        tvCounterR.visibility = View.INVISIBLE
    }

    fun deleteTimer(){
        if (!running){  //If its not running
            println("Running Check")
            if (intervals.size >= 2){   //Check for more than one intervals
                println("Interval size check")
                println("In the bubble check")
                intervals.removeAt(currentCounter-1)
                setNew = true
                if(currentCounter-1 == intervals.size){     //If on the last thing
                    tvCounterR.visibility = View.INVISIBLE
                    tvCountDownR.visibility = View.INVISIBLE
                    moveRight()

                    //Update everything to move
                    tvCounterC.text = (currentCounter).toString()
                    tvCounterL.text = (currentCounter - 1).toString()
                }
                else {
                    currentCounter -= 1
                    moveLeft()

                    //Update everything to move
                    tvCounterC.text = (currentCounter).toString()
                    tvCounterR.text = (currentCounter + 1).toString()
                }

            }
        } else{
            Toast.makeText(this, "Please pause the timer first", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCountDownText() {
        if (setNew) {
            timeLeftInMillis = intervals[currentCounter - 1]
            if (moveDir){
                if (intervals.size > currentCounter) {
                    timeLeftInMillisR = intervals[currentCounter]
                }
                if (currentCounter != 1) {
                    timeLeftInMillisL = intervals[currentCounter - 2]
                }
            }
            setNew = false
        }
        minutes = ((timeLeftInMillis / 1000) / 60).toInt()
        seconds = ((timeLeftInMillis / 1000) % 60).toInt()

        //Locale correct placement?
        timeLeftFormatted = "%02d:%02d".format(minutes, seconds, Locale.getDefault())
        tvCountDownC.text = timeLeftFormatted

        if (moveDir){
            minutes = ((timeLeftInMillisL / 1000) / 60).toInt()
            seconds = ((timeLeftInMillisL / 1000) % 60).toInt()

            timeLeftFormatted = "%02d:%02d".format(minutes, seconds, Locale.getDefault())
            tvCountDownL.text = timeLeftFormatted


            minutes = ((timeLeftInMillisR / 1000) / 60).toInt()
            seconds = ((timeLeftInMillisR / 1000) % 60).toInt()

            timeLeftFormatted = "%02d:%02d".format(minutes, seconds, Locale.getDefault())
            tvCountDownR.text = timeLeftFormatted

            timeLeftInMillisL = 0
            timeLeftInMillisR = 0
            moveDir = false

        }

    }

    //Sets the pickers from 0 to 9
    fun setVals(pickers: List<NumberPicker>) {
        for (x in pickers) {
            if (x == pickers.elementAt(2)) {
                x.minValue = 0
                x.maxValue = 5
                x.value = 0
            } else {
                x.minValue = 0
                x.maxValue = 9
                x.value = 0
            }
        }
    }

    fun setTimer() {
        if (running == false) {
            var x: Long = 0

            x += pickerML.value * 600000
            x += pickerMR.value * 60000
            x += pickerSL.value * 10000
            x += pickerSR.value * 1000

            intervals[currentCounter - 1] = x
            setNew = true
            updateCountDownText()
        } else {
            Toast.makeText(this, "Please Stop the timer first", Toast.LENGTH_SHORT).show()
        }
    }

    fun addInterval() {
        intervals.add(0)
        if (intervals.size >= 1) {
            tvCountDownR.visibility = View.VISIBLE
            tvCounterR.visibility = View.VISIBLE
        }
    }

    //Moves the time intervals one to the right
    fun moveRight() {
//        if (!running or (running and movingNext)) {       //could this help with moving right while timer is running?
//            movingNext = false
        if (!running){
            if (currentCounter > 1) {
                if (currentCounter == intervals.size){
                    tvCountDownR.visibility = View.VISIBLE
                    tvCounterR.visibility = View.VISIBLE
                }

                currentCounter -= 1

                if (currentCounter == 1) {
                    tvCountDownL.visibility = View.INVISIBLE
                    tvCounterL.visibility = View.INVISIBLE

                }
                setNew = true
                moveDir = true
                updateCountDownText()
                tvCounterC.text = currentCounter.toString()
                tvCounterL.text = (currentCounter - 1).toString()
                tvCounterR.text = (currentCounter + 1).toString()

                np_minL.value = 0
                np_minR.value = 0
                np_secL.value = 0
                np_secR.value = 0
            }
        }else {
            Toast.makeText(this, "Please pause the timer first", Toast.LENGTH_SHORT).show()
        }
    }

    //Moves the time intervals one to the left
    fun moveLeft() {
        if (!running or (running and movingNext)) {
            movingNext = false
            if (currentCounter < intervals.size) {
                if (currentCounter == 1) {
                    tvCountDownL.visibility = View.VISIBLE
                    tvCounterL.visibility = View.VISIBLE
                }
                currentCounter += 1

                if (currentCounter == intervals.size) {
                    tvCountDownR.visibility = View.INVISIBLE
                    tvCounterR.visibility = View.INVISIBLE
                }
                setNew = true
                moveDir = true
                updateCountDownText()
                tvCounterC.text = currentCounter.toString()
                tvCounterL.text = (currentCounter - 1).toString()
                tvCounterR.text = (currentCounter + 1).toString()

                np_minL.value = 0
                np_minR.value = 0
                np_secL.value = 0
                np_secR.value = 0
            }
        }else{
            Toast.makeText(this, "Please pause the timer first", Toast.LENGTH_SHORT).show()
        }
    }

}

