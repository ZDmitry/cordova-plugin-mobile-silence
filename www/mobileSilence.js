var exec    = require('cordova/exec'),
    utils   = require('cordova/utils'),
    channel = require('cordova/channel');
    

function _eventNameByMethod(name) {
    return ( 'onMobileSilence' + name.substr(0,1).toUpperCase() + name.substr(1) );
}


var MobileSilence = function() {  // Constructor
    this._wifiStatus = false;
    this._wwanStatus = false;
};

MobileSilence.init = function(debug, clbk) {  // successCallback, errorCallback, message, forceAsync
    exec(success, error, 'MobileSilence', 'init', [debug]);

    function success(msg) {
        if ( msg && msg.method == 'init' ) {
            if (clbk) clbk(null, true);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.volumeMute = function(enabled, clbk) { 
    exec(success, error, 'MobileSilence', 'volumeMute', [enabled]);

    function success(msg) {
        if ( msg && msg.method == 'volumeMute' ) {
            if (clbk) clbk(null, true);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.isMobileDataEnabled = function(clbk) { 
    exec(success, error, 'MobileSilence', 'isMobileDataEnabled', []);

    function success(msg) {
        if ( msg && msg.method == 'isMobileDataEnabled' ) {
            if (clbk) clbk(null, msg.result);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence._setMobileDataEnabled = function(enabled, clbk) { 
    exec(success, error, 'MobileSilence', 'setMobileDataEnabled', [enabled]);

    function success(msg) {
        if ( msg && msg.method == 'setMobileDataEnabled' ) {
            if (clbk) clbk(null, msg.success);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.setMobileDataEnabled = function(enabled, clbk) {
    this.isMobileDataEnabled(function(enmod){
        this._wwanStatus = enmod;
        this._setMobileDataEnabled(enabled,clbk);
    }.bind(this));
};

MobileSilence.restoreMobileDataEnabled = function(clbk) {
    this._setMobileDataEnabled(this._wwanStatus, clbk);
};

MobileSilence.isWifiDataEnabled = function(clbk) { 
    exec(success, error, 'MobileSilence', 'isWifiDataEnabled', []);

    function success(msg) {
        if ( msg && msg.method == 'isWifiDataEnabled' ) {
            if (clbk) clbk(null, msg.result);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence._setWifiDataEnabled = function(enabled, clbk) { 
    exec(success, error, 'MobileSilence', 'setWifiDataEnabled', [enabled]);

    function success(msg) {
        if ( msg && msg.method == 'setWifiDataEnabled' ) {
            if (clbk) clbk(null, msg.success);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.setWifiDataEnabled = function(enabled, clbk) {
    this.isWifiDataEnabled(function(enmod){
        this._wifiStatus = enmod;
        this._setWifiDataEnabled(enabled,clbk);
    }.bind(this));
};

MobileSilence.restoreWifiDataEnabled = function(clbk) {
    this._setWifiDataEnabled(this._wifiStatus, clbk);
};

MobileSilence.setAirplaneModeEnabled = function(enabled, clbk) {
    exec(success, error, 'MobileSilence', 'setAirplaneModeEnabled', [enabled]);

    function success(msg) {
        if ( msg && msg.method == 'setAirplaneModeEnabled' ) {
            if (clbk) clbk(null, msg.success);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.setBlockCallsEnabled = function(enabled, clbk) {
    exec(success, error, 'MobileSilence', 'setBlockCallsEnabled', [enabled]);

    function success(msg) {
        if ( msg && msg.method == 'setBlockCallsEnabled' ) {
            if (clbk) clbk(null, msg.success);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

MobileSilence.showSettingsPage = function(clbk) {
    exec(success, error, 'MobileSilence', 'showSettingsPage', []);

    function success(msg) {
        if ( msg && msg.method == 'showSettingsPage' ) {
            if (clbk) clbk(null, msg.success);
        }
    }

    function error(msg) {
        if (clbk) clbk(false);
    }
};

channel.onCordovaReady.subscribe( function() {
    // Will be called on cordova loaded
    MobileSilence.init();
});

module.exports = MobileSilence;
