package com.cordova.zdmitry.mobilesilence;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.Context;
import android.content.SharedPreferences;

import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;

import android.util.Log;


public class PhoneCallReceiver extends PhoneStateListener {
    private static  final String TAG = PhoneCallReceiver.class.getName();
    private Context    m_context = null;

    private static final String PREFS_FILENAME    = "CordovaMobileSilence.conf";
    private static final String PREFS_BLOCK_CALLS = "blockCalls";

    public PhoneCallReceiver(final Context context) {
        m_context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incoming){
        switch (state){
            case TelephonyManager.CALL_STATE_IDLE:
                Log.v(TAG, "[CALL_STATE_IDLE]");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.v(TAG, "[CALL_STATE_OFFHOOK]");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.v(TAG, "[CALL_STATE_RINGING] = " + incoming);
                this.endCall(incoming);
                break;
            default:
                break;
        }
    }

    private void endCall(final String callerNumber) {
        if ( m_context == null ) return;

        SharedPreferences settings = m_context.getSharedPreferences(PREFS_FILENAME, 0);
        boolean blockCalls = settings.getBoolean(PREFS_BLOCK_CALLS, false);

        if (blockCalls) {
            TelephonyManager telephony = (TelephonyManager)m_context.getSystemService(Context.TELEPHONY_SERVICE);

            try {
                Class  c = Class.forName(telephony.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);

                ITelephony telephonyService = (ITelephony)m.invoke(telephony);
                //telephonyService.silenceRinger();
                telephonyService.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
