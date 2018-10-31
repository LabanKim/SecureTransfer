package com.fusion.kim.alc30;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MessagingActivity extends AppCompatActivity {

    private TextView mNameTv, mMessageTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mNameTv = findViewById(R.id.tv_message_sender);

        mNameTv.setText(getIntent().getStringExtra("senderName"));

    }
}
