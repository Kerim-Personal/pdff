package com.example.pdf

import android.content.Intent
import android.os.*
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var textViewSplash: TextView
    private val typingDelay: Long = 100L
    private val textToType = "Codenzi"
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        textViewSplash = findViewById(R.id.textViewSplash)
        textViewSplash.text = ""

        startTypingAnimation()

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }

    private fun startTypingAnimation() {
        if (currentIndex <= textToType.lastIndex) {
            val builder = SpannableStringBuilder()
            for (i in 0..currentIndex) {
                builder.append(textToType[i])
                if (i == currentIndex) {
                    builder.setSpan(
                        AlphaAnimationSpan(textViewSplash),
                        i,
                        i + 1,
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            textViewSplash.text = builder
            currentIndex++
            handler.postDelayed({ startTypingAnimation() }, typingDelay)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
