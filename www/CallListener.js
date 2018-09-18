var exec = require('cordova/exec');

// 获取指定手机号码的通话记录
exports.getCallInfo = function (successCallback, mobile) {
    exec(successCallback, null, 'CallListener', 'getCallInfo', [mobile]);
};

// 添加电话状态监听
exports.addListener = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'CallListener', 'addListener', []);
};

// 呼叫指定的手机号码
exports.callMobile = function (mobile) {
    exec(null, null, 'CallListener', 'callMobile', [mobile]);
};
