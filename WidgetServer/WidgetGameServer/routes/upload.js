var express = require('express');
var router = express.Router();
var multer = require('multer');
var fs = require('fs');
var upload = multer({
    dest: './images'
});

/* GET users listing. */

router.post('/userImageFile', upload.any(), function(req, res, next) {
    console.log('upload');
    console.log('upload', req.body);
    var filesLength = req.files.length;
    console.log('upload', filesLength);
    var uploadCnt = 0;
    if (filesLength <= 0) {
        res.json({
            code: 200,
            errorMessage: "there is no files"
        });
    } else {
        console.log('upload1');
        var fileNames = req.body.fileNames.split('/');
        imageUpload(req.files, fileNames, 0, filesLength, "", fs, function(result) {
            if (result === true) {
                res.json({
                    code: 100
                });
            }
        });
    }
});

function imageUpload(filesArr, fileNames, index, filesLength, fileFolder, fs, next) {
    console.log('upload2');
    file = filesArr[index];
    fileName = fileNames[index];
    var forTime = new Date();
    //files.originalname = forTime.getTime() + files.originalname;

    console.log('upload3');
    fs.rename(file.path, __dirname + '\\..\\public\\images\\' + fileFolder + fileName, function(err) {

        console.log('upload4');
        if (err) {
            throw err;
        } else {
            index++;
            if (index == filesLength) {
                next(true);
            } else {
                imageUpload(filesArr, fileNames, index, filesLength, fileFolder, fs, next);
            }
        }
    });
}

module.exports = router;
