var exec = require('cordova/exec');

exports.show = function () {
    exec(null, null, 'CallListener', 'show', []);
};
