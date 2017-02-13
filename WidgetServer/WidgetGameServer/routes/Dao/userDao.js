var mongojs = require('mongojs');
var db = mongojs('TxT', ['users']);

exports.findUser = function(findJson, exJson, next) {
    db.users.findOne(findJson, exJson, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}

exports.insertUser = function(insertJson, next) {
    db.users.insert(insertJson, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}

exports.updateUser = function(findJson, updateJson, next) {
    console.log("updateUser", findJson);
    console.log("updateUser", updateJson);
    db.users.update(findJson, {
        $set: updateJson
    }, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}
