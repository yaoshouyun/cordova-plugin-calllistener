var exec = require('cordova/exec');

var CallListener = {

    show : function (msg) {
        exec(null, null, 'CallListener', 'show', [msg]);
    },

}

module.exports = CallListener;
