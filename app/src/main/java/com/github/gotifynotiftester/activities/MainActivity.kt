package com.github.gotifynotiftester.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.gotifynotiftester.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goToCheckList(view: View){
        val intent = Intent(this,
            CheckActivity::class.java)
        startActivity(intent)
    }

}
