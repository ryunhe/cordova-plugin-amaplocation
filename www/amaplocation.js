var exec = require("cordova/exec");

module.exports = {
    getCurrentPosition: function(success, failure, config) {
        exec(success || function() {},
             failure || function() {},
             'AmapLocation',
             'getCurrentPosition',
             []);
    }
};