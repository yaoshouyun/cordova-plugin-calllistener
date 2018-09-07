var exec = require('cordova/exec');

exports.getCallInfo = function (successCallback,  mobile) {
    exec(successCallback, null, 'CallListener', 'getCallInfo', [mobile]);
};

exports.addListener = function (successCallback,errorCallback) {
    exec(successCallback, errorCallback, 'CallListener', 'addListener', []);
};

