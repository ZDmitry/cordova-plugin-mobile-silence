package com.cordova.zdmitry.mobilesilence;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Application;

import android.os.Handler;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.Context;

import android.media.AudioManager;

import android.provider.Settings;


public class MobileSilence extends CordovaPlugin {
    private static final String TAG    = MobileSilence.class.getName();
    private static Application  m_app  = null;

    private static final long   RUN_DELAY_TIME = 15 * 1000;

    private CallbackContext     m_cbContext = null;   // callback for events

    private int     m_ringerMode   = AudioManager.RINGER_MODE_NORMAL;
    private int     g_ringerMode   = AudioManager.RINGER_MODE_NORMAL;

    private boolean g_wwanEnabled  = true;
    private boolean g_wifiEnabled  = true;

    private boolean m_wwanEnabled  = true;
    private boolean m_wifiEnabled  = true;
    private boolean m_callBlocked  = false;

    private Handler  m_restoreTimer   = null;
    private Runnable m_restoreService = null;


    @Override
    protected void pluginInitialize() {
        Log.v(TAG, TAG + "::pluginInitialize(void)");

        m_app = this.cordova.getActivity().getApplication();

        ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());

        AudioManager audiomanage = (AudioManager)m_app.getSystemService(Context.AUDIO_SERVICE);
        m_ringerMode  = g_ringerMode  = audiomanage.getRingerMode();

        m_wwanEnabled = g_wwanEnabled = conn.isMobileDataEnabled();
        m_wifiEnabled = g_wifiEnabled = conn.isWifiDataEnabled();

        conn.setBlockCallsEnabled(false);
    }

    private void restoreConnectivity(){
        Log.v(TAG, TAG + "::restoreConnectivity(void)");

        ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());

        AudioManager audiomanage = (AudioManager)m_app.getSystemService(Context.AUDIO_SERVICE);
        audiomanage.setRingerMode(g_ringerMode);

        conn.setMobileDataEnabled(g_wwanEnabled);
        conn.setWifiDataEnabled(g_wifiEnabled);

        conn.setBlockCallsEnabled(false);
    }

    @Override
    public void onStart() {
        Log.v(TAG, TAG + "::onStart(void)");
    }

    @Override
    public void onStop() {
        Log.v(TAG, TAG + "::onStop(void)");
    }

    @Override
    public void onPause(boolean multitasking) {
        Log.v(TAG, TAG + "::onPause(bool)");

        final MobileSilence self = this;
        m_restoreService = new Runnable() {
            public void run() {
                self.restoreConnectivity();
                self.m_restoreTimer   = null;
                self.m_restoreService = null;
            }
        };

        m_restoreTimer = new Handler();
        m_restoreTimer.postDelayed(m_restoreService, RUN_DELAY_TIME);
    }

    @Override
    public void onResume(boolean multitasking) {
        Log.v(TAG, TAG + "::onResume(bool)");

        if ( m_restoreTimer == null ) {
            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());

            AudioManager audiomanage = (AudioManager) m_app.getSystemService(Context.AUDIO_SERVICE);
            audiomanage.setRingerMode(m_ringerMode);

            conn.setMobileDataEnabled(m_wwanEnabled);
            conn.setWifiDataEnabled(m_wifiEnabled);

            conn.setBlockCallsEnabled(m_callBlocked);
        } else {
            m_restoreTimer.removeCallbacks(m_restoreService);

            m_restoreTimer   = null;
            m_restoreService = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, TAG + "::onDestroy(bool)");
        this.restoreConnectivity();
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback context from which we were invoked.
     */
    @SuppressLint("NewApi")
    public boolean execute( String action, final JSONArray args, final CallbackContext callbackContext ) throws JSONException {
        if ( m_cbContext == null ) m_cbContext = callbackContext;

        if ( action.equals("init") ) {
            PluginResult result;

            JSONObject obj = new JSONObject();
            obj.put("method", "init");
            obj.put("success", true);

            result = new PluginResult(PluginResult.Status.OK, obj);

            result.setKeepCallback(true);
            m_cbContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("volumeMute") ) {
            final boolean enabled = args.getBoolean(0);
            PluginResult result;

            JSONObject obj = new JSONObject();
            obj.put("method", "volumeMute");
            obj.put("success", true);

            result = new PluginResult(PluginResult.Status.OK, obj);

            AudioManager audiomanage = (AudioManager)m_app.getSystemService(Context.AUDIO_SERVICE);

            m_ringerMode = (enabled ? AudioManager.RINGER_MODE_SILENT : g_ringerMode);
            audiomanage.setRingerMode(m_ringerMode);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("isMobileDataEnabled") ) {
            PluginResult result;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            boolean retVal = conn.isMobileDataEnabled();

            JSONObject obj = new JSONObject();
            obj.put("method", "isMobileDataEnabled");
            obj.put("result", retVal);
            obj.put("success", true);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("setMobileDataEnabled") ) {
            final boolean enabled = args.getBoolean(0);
            PluginResult result;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            boolean retVal = conn.setMobileDataEnabled(enabled);
            m_wwanEnabled = enabled;

            JSONObject obj = new JSONObject();
            obj.put("method", "setMobileDataEnabled");
            obj.put("success", retVal);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("isWifiDataEnabled") ) {
            PluginResult result;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            boolean retVal = conn.isWifiDataEnabled();

            JSONObject obj = new JSONObject();
            obj.put("method", "isWifiDataEnabled");
            obj.put("result", retVal);
            obj.put("success", true);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("setWifiDataEnabled") ) {
            final boolean enabled = args.getBoolean(0);
            PluginResult result;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            boolean retVal = conn.setWifiDataEnabled(enabled);
            m_wifiEnabled  = enabled;

            JSONObject obj = new JSONObject();
            obj.put("method", "setWifiDataEnabled");
            obj.put("success", retVal);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("setAirplaneModeEnabled") ) {
            final boolean enabled = args.getBoolean(0);

            PluginResult result;
            boolean retVal;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            retVal = conn.setAirplaneMode(enabled);

            JSONObject obj = new JSONObject();
            obj.put("method", "setAirplaneModeEnabled");
            obj.put("success", retVal);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("setBlockCallsEnabled") ) {
            final boolean enabled = args.getBoolean(0);

            PluginResult result;
            boolean retVal;

            ConnectivityManager conn = new ConnectivityManager(m_app.getApplicationContext());
            retVal = conn.setBlockCallsEnabled(enabled);
            m_callBlocked = enabled;

            JSONObject obj = new JSONObject();
            obj.put("method", "setBlockCallsEnabled");
            obj.put("success", retVal);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        if ( action.equals("showSettingsPage") ) {
            PluginResult result;
            boolean retVal = false;

            if ( this.cordova != null ) {
                Activity activity = this.cordova.getActivity();
                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(viewIntent);
                retVal = true;
            }

            JSONObject obj = new JSONObject();
            obj.put("method", "showSettingsPage");
            obj.put("success", retVal);

            result = new PluginResult(PluginResult.Status.OK, obj);

            // result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        }

        return false;
    }

}
