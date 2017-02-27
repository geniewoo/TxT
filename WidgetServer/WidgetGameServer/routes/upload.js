var express = require('express');
var path = require('path');
var router = express.Router();
var multer = require('multer');
var fs = require('fs');
var async = require('async');
var gameDao = require('./Dao/gameDao');
var upload = multer({
    dest: './images'
});

/* GET users listing. */

router.post('/userImageFile', upload.any(), function(req, res, next) {
    var filesLength = req.files.length;
    if (filesLength <= 0) {
        res.json({
            code: 200,
            errorMessage: "there is no files"
        });
    } else {
        var fileNames = req.body.fileNames.split('/');
        uploadImage(req.files, fileNames, 0, filesLength, "profile" + path.sep, fs, function(result) {
            if (result === true) {
                res.json({
                    code: 100
                });
            } else {
                res.json({
                    code: 500
                });
            }
        });
    }
});

router.post('/game/images', upload.any(), function(req, res, next) {
    var filesLength = req.files.length;
    if (filesLength <= 0) {
        res.json({
            code: 200,
            errorMessage: "there is no files"
        });
    } else {
        var nickname = req.query.nickname;
        var gameTitle = req.query.gameTitle;
        async.waterfall([
                function(callback) {
                    fs.access(__dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + nickname, function(err) {
                        if (err && err.code === 'ENOENT') {
                            fs.mkdir(__dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + nickname, function(err) {
                                callback(null);
                            });
                        } else {
                            callback(null);
                        }
                    });
                },
                function(callback) {
                    fs.access(__dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + nickname + path.sep + gameTitle, function(err) {
                        if (err && err.code === 'ENOENT') {
                            fs.mkdir(__dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + nickname + path.sep + gameTitle, function() {
                                callback(null);
                            });
                        } else {
                            callback(null);
                        }
                    });
                },
                function(callback) {
                    uploadImages(req.files, 0, filesLength, nickname + path.sep + gameTitle + path.sep, fs, function(result) {
                        if (result === true) {
                            callback(null);
                        } else {
                            callback(true);
                        }
                    });
                }
            ],
            function(err) {
                if (err) {
                    res.json({
                        code: 500,
                        errorMessage: "server error"
                    });
                } else {
                    res.json({
                        code: 100
                    });
                }
            }
        );
    }
});

router.post('/game/fullGameRepo', function(req, res, next) {
    var nickname = req.body.maker.nickName;
    var title = req.body.gameInfo.gameTitle;
    var _id = nickname + "+_+" + title;

    gameDao.saveGame({
        _id : _id,
        nickname : nickname,
        title : title,
        TextGameInfo : req.body
    }, function(result){
        if (result) {
            res.json({
                code: 100
            });
        } else {
            res.json({
                code: 500,
                errorMessage: "server_error"
            });
        }
    });

});

function uploadImage(filesArr, fileNames, index, filesLength, fileFolder, fs, next) {
    file = filesArr[index];
    fileName = fileNames[index];
    var forTime = new Date();

    fs.rename(file.path, __dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + fileFolder + fileName, function(err) {
        if (err) {
            throw err;
        } else {
            index++;
            if (index == filesLength) {
                next(true);
            } else {
                uploadImage(filesArr, fileNames, index, filesLength, fileFolder, fs, next);
            }
        }
    });
}

function uploadImages(filesArr, index, filesLength, fileFolder, fs, next) {
    file = filesArr[index];
    var forTime = new Date();


    fs.rename(file.path, __dirname + path.sep + '..' + path.sep + 'public' + path.sep + 'images' + path.sep + fileFolder + file.originalname, function(err) {
        if (err) {
            throw err;
        } else {
            index++;
            if (index == filesLength) {
                next(true);
            } else {
                uploadImages(filesArr, index, filesLength, fileFolder, fs, next);
            }
        }
    });
}


module.exports = router;
