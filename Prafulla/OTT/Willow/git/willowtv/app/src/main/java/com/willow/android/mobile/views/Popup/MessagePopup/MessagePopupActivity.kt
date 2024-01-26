package com.willow.android.mobile.views.popup.messagePopup

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.willow.android.R

class MessagePopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_activity_message)

        val title = intent.getStringExtra("TITLE")
        val message = intent.getStringExtra("MESSAGE")

        val messageTitle = findViewById<TextView>(R.id.message_title)
        val messageText = findViewById<TextView>(R.id.message_text)
        val okButton = findViewById<Button>(R.id.ok_button)

        messageTitle.text = title
        messageText.text = message

        okButton.setOnClickListener {
            finish()
        }
    }
}