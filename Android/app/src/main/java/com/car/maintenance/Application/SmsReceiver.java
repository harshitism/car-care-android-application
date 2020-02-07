package com.car.maintenance.Application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                str += "SMS from " + msgs[i].getOriginatingAddress() + " : ";
                // Fetch the text message
                str += msgs[i].getMessageBody().toString();
                // Newline <img draggable="false" class="emoji" alt="🙂" src="https://s.w.org/images/core/emoji/72x72/1f642.png">
                str += "\n";
            }

            // Display the entire SMS Message
            Log.d(TAG, str);
            if (msgs[0].getOriginatingAddress().contains("VAAHAN")) {
                JSONObject message = new JSONObject();
                try {
                    message.put("sender", msgs[0].getOriginatingAddress());
                    message.put("msg", msgs[0].getMessageBody());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("sender", message.toString());
                Intent bintent = new Intent("sms_received");
                bintent.putExtra("message", message.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(bintent);
            }
        }
    }
}
