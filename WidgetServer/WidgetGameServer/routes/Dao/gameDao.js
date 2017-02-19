var mongojs = require('mongojs');
var db = mongojs('TxT', ['games']);

exports.findGame = function(findJson, exJson, next) {
    db.games.findOne(findJson, exJson, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}

exports.findGames = function(findJson, exJson, sortJson, skip, limitNum, next) {
    db.games.find(findJson, exJson).sort(sortJson).skip(skip).limit(limitNum, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}

exports.saveGame = function(saveJson, next) {
    db.games.save(saveJson, function(error, data) {
        if (error) {
            next(false);
        } else {
            next(data);
        }
    });
}