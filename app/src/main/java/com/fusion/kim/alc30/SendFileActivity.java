package com.fusion.kim.alc30;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SendFileActivity extends AppCompatActivity {

    private Button mSendFileBtn;
    private TextView mMessageTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        mSendFileBtn = findViewById(R.id.btn_send_file);
        mMessageTv = findViewById(R.id.input_text_file);

        mSendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(mMessageTv.getText().toString().trim())){

                    Toast.makeText(SendFileActivity.this, "Please enter a message", Toast.LENGTH_LONG).show();
                    return;

                } else {

                    String message = mMessageTv.getText().toString().trim();

                    Intent messageIntent = new Intent(SendFileActivity.this, PickPersonActivity.class);
                    messageIntent.putExtra("message", message);
                    startActivity(messageIntent);

                }


            }
        });

    }
}
