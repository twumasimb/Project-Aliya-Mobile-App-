package com.example.aliyamessenger;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSION_REQUEST_CODE = 1122;
    final static String SENT_MSG_FLAG = "SENT_MSG_FLAG";
    final static String DELIVERED_MSG_FLAG = "DELIVERED_MSG_FLAG";
    private Button btnMsg1;

    PendingIntent sentPendingIntent;
    PendingIntent deliveredPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnMsg1 = findViewById(R.id.btn_msg1);

        btnMsg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndSendSMS();
            }
        });

        Intent sentIntent = new Intent(SENT_MSG_FLAG);
        Intent deliveredIntent = new Intent(DELIVERED_MSG_FLAG);

        sentPendingIntent = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, deliveredIntent, 0);

        // Broadcast Receiver to know if notification has been sent.
        BroadcastReceiver sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(MainActivity.this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Broadcast Receiver to know if notification has been delivered.
        BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(MainActivity.this, "SMS delivered successfully", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver(sentReceiver, new IntentFilter(SENT_MSG_FLAG));
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED_MSG_FLAG));
    }

    private void checkPermissionAndSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        }
    }

    private void sendSMS() {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("+233542974219", null, "Just testing some things", sentPendingIntent, deliveredPendingIntent);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(MainActivity.this, "The app does not have SMS permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
