package com.fusion.kim.alc30;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SendFileActivity extends AppCompatActivity {

    private Button mSendFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        mSendFileBtn = findViewById(R.id.btn_send_file);

        mSendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SendFileActivity.this, PickPersonActivity.class));

            }
        });

    }
}
