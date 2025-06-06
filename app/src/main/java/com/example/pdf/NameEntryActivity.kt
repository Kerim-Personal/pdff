// kerim-personal/pdff/pdff-8e15915c2ac2245f8364926638cf68db80017906/app/src/main/java/com/example/pdf/NameEntryActivity.kt
package com.example.pdf

import android.content.Context // Import Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class NameEntryActivity : AppCompatActivity() {

    // Add this override to ensure locale is applied when the activity is attached to its context
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_entry)

        val editTextName: TextInputEditText = findViewById(R.id.editTextName)
        val buttonContinue: Button = findViewById(R.id.buttonContinue)

        buttonContinue.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isNotEmpty()) {
                // Save the name and proceed to the main screen
                SharedPreferencesManager.saveUserName(this, name)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_name_toast), Toast.LENGTH_SHORT).show()
            }
        }
    }
}