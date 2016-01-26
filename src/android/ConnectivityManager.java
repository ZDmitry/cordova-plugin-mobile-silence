package com.cordova.zdmitry.mobilesilence;

import android.net.wifi.WifiManager;

import android.content.Context;
import android.content.SharedPreferences;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ConnectivityManager {
    private static final  String TAG  = ConnectivityManager.class.getName();
    private Context       m_context   = null;   // callback for events

    private static final String PREFS_FILENAME    = "CordovaMobileSilence.conf";
    private static final String PREFS_BLOCK_CALLS = "blockCalls";


    public ConnectivityManager(Context context) {
        m_context = context;
    }

    public boolean setBlockCallsEnabled(boolean enabled) {
        SharedPreferences settings = m_context.getSharedPreferences(PREFS_FILENAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_BLOCK_CALLS, enabled);
        boolean prefSaved = editor.commit();

        if ( prefSaved && m_context != null ) {
            TelephonyManager telephony = (TelephonyManager)m_context.getSystemService(Context.TELEPHONY_SERVICE);

            if (enabled) {
                telephony.listen(new PhoneCallReceiver(m_context), PhoneStateListener.LISTEN_CALL_STATE);
            } else {
                telephony.listen(null,  PhoneStateListener.LISTEN_NONE);
            }

            return true;
        }

        return false;
    }

    public boolean setAirplaneMode(boolean enabled) {
        try {
            final Class<?> conmanClass = Class.forName(m_context.getSystemService(Context.CONNECTIVITY_SERVICE).getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");

            iConnectivityManagerField.setAccessible(true);

            final Object   iConnectivityManager      = iConnectivityManagerField.get(m_context.getSystemService(Context.CONNECTIVITY_SERVICE));
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());

            final Method setAirplaneMode = iConnectivityManagerClass.getDeclaredMethod("setAirplaneMode", Boolean.TYPE);

            setAirplaneMode.setAccessible(true);
            setAirplaneMode.invoke(iConnectivityManager, enabled);

            /* final Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
            for ( final Method method : methods ) {
                if ( method.toGenericString().contains("set") ) {
                    Log.i("TESTING", "Method: " + method.getName());
                }
            } */

            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setWifiDataEnabled( boolean enabled ) {
        WifiManager wifiManager = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
        return true;
    }

    public boolean isWifiDataEnabled() {
        WifiManager wifiManager = (WifiManager)m_context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public boolean setMobileDataEnabled( boolean enabled ) {
        Object   iConnectivityManager;
        Class<?> iConnectivityManagerClass;
        Method   setMobileDataEnabled = null;

        try {
            final Class<?> conmanClass = Class.forName(m_context.getSystemService(Context.CONNECTIVITY_SERVICE).getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");

            iConnectivityManagerField.setAccessible(true);

            iConnectivityManager      = iConnectivityManagerField.get(m_context.getSystemService(Context.CONNECTIVITY_SERVICE));
            iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());

            /* final Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
            for ( final Method method : methods ) {
                if ( method.toGenericString().contains("set") ) {
                    Log.i("TESTING", "Method: " + method.toGenericString());
                    if ( method.getName().equals("setMobileDataEnabled") ) {
                        setMobileDataEnabled = method;
                        break;
                    }
                }
            } */
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }

        try {
            setMobileDataEnabled = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            setMobileDataEnabled.setAccessible(true);
            setMobileDataEnabled.invoke(iConnectivityManager, enabled);
        } catch ( Exception e ) {
            // ...
        }

        if ( setMobileDataEnabled == null ) {
            try {
                setMobileDataEnabled = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", String.class, Boolean.TYPE);

                setMobileDataEnabled.setAccessible(true);
                setMobileDataEnabled.invoke(iConnectivityManager, m_context.getPackageName(), enabled);
            } catch ( Exception e ) {
                e.printStackTrace();
                // ...
            }
        }

        return ( setMobileDataEnabled != null );
    }

    public boolean isMobileDataEnabled() {
        Object   iConnectivityManager;
        Class<?> iConnectivityManagerClass;
        Method   setMobileDataEnabled = null;

        try {
            final Class<?> conmanClass = Class.forName(m_context.getSystemService(Context.CONNECTIVITY_SERVICE).getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");

            iConnectivityManagerField.setAccessible(true);

            iConnectivityManager      = iConnectivityManagerField.get(m_context.getSystemService(Context.CONNECTIVITY_SERVICE));
            iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());

            /* final Method[] methods = iConnectivityManagerClass.getDeclaredMethods();
            for ( final Method method : methods ) {
                if ( method.toGenericString().contains("set") ) {
                    Log.i("TESTING", "Method: " + method.toGenericString());
                    if ( method.getName().equals("setMobileDataEnabled") ) {
                        setMobileDataEnabled = method;
                        break;
                    }
                }
            } */
        } catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }

        boolean result = false;

        try {
            setMobileDataEnabled = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");

            setMobileDataEnabled.setAccessible(true);
            result = (Boolean)setMobileDataEnabled.invoke(iConnectivityManager);
        } catch ( Exception e ) {
            // ...
        }

        if ( setMobileDataEnabled == null ) {
            try {
                setMobileDataEnabled = iConnectivityManagerClass.getDeclaredMethod("getMobileDataEnabled", String.class);

                setMobileDataEnabled.setAccessible(true);
                result = (Boolean)setMobileDataEnabled.invoke(iConnectivityManager, m_context.getPackageName());
            } catch ( Exception e ) {
                e.printStackTrace();
                // ...
            }
        }

        return result;
    }

}
