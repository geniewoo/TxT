var express = require('express');
var router = express.Router();
var multer = require('multer');
var fs = require('fs');
var upload = multer({
    dest: './images'
});

/* GET users listing. */

router.post('/userImageFile', upload.any(), function(req, res, next) {
    var filesLength = req.files.length;
    var uploadCnt = 0;
    if (filesLength <= 0) {
        res.json({
            code: 200,
            errorMessage: "there is no files"
        });
    } else {
        console.log('upload1');
        var fileNames = req.body.fileNames.split('/');
        uploadImage(req.files, fileNames, 0, filesLength, "", fs, function(result) {
            if (result === true) {
                res.json({
                    code: 100
                });
            }
        });
    }
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

module.exports = router;
