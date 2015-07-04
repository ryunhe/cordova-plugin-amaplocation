var exec = require("cordova/exec");

module.exports = {
    start: function(success, failure, config) {
        exec(success || function() {},
             failure || function() {},
             'AmapLocation',
             'start',
             []);
    },
    stop: function(success, failure, config) {
        exec(success || function() {},
            failure || function() {},
            'AmapLocation',
            'stop',
            []);
    }
};
