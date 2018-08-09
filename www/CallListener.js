var exec = require('cordova/exec');

exports.getCallTime = function (successCallback,  mobile) {
    exec(successCallback, null, 'CallListener', 'getCallTime', [mobile]);
};

exports.addListener = function (successCallback) {
    exec(successCallback, null, 'CallListener', 'addListener', []);
};

