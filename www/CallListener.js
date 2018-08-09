var exec = require('cordova/exec');

exports.getCallTime = function (successCallback, errorCallback, mobile) {
    exec(successCallback, errorCallback, 'CallListener', 'getCallTime', [mobile]);
};

exports.listener = function (successCallback) {
    exec(successCallback, null, 'CallListener', 'listener', []);
};

