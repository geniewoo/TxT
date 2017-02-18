var express = require('express');
var router = express.Router();
var userDao = require('./Dao/userDao');
var crypto = require('./Common/crypto');

/* GET users listing. */
router.get('/login/test', function(req, res, next) {
    var email = req.query.email;
    var password = crypto.getCrypto(req.query.password);
    userDao.findUser({
        email: email,
        password: password
    }, {}, function(result) {
        if (result === false) {
            res.json({
                code: 500,
                errorMessage: "server error"
            });
        } else if (!result) {
            res.json({
                code: 200,
                errorMessage: "user is not exists"
            });
        } else if (result) {
            res.json({
                code: 100,
                nickname: result.nickname,
                imageUrl: result.imageUrl
            });
        }
    });
});
router.get('/join/test', function(req, res, next) {
    var email = req.query.email;
    var password = req.query.password;
    var nickname = req.query.nickname;
    if (!(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/).test(email)) {
        res.json({
            code: 400,
            errorMessage: "invalid email format"
        });
    } else if (!(/^[A-Za-z0-9!@#$%]{6,20}$/).test(password)) {
        res.json({
            code: 400,
            errorMessage: "invalid password format"
        });
    } else if (!(/^[A-Za-z0-9가-힣_]{2,16}$/).test(nickname)) {
        res.json({
            code: 400,
            errorMessage: "invalid nickname format"
        });
    } else {
        password = crypto.getCrypto(password);
        userDao.findUser({
            email: email
        }, {}, function(result) {
            if (result === false) {
                res.json({
                    code: 500,
                    errorMessage: "server error"
                });
            } else if (result) {
                res.json({
                    code: 200,
                    errorMessage: "duplicate email"
                });
            } else if (!result) {
                userDao.findUser({
                    nickname: nickname
                }, {}, function(result) {
                    if (result === false) {
                        res.json({
                            code: 500,
                            errorMessage: "server error"
                        });
                    } else if (result) {
                        res.json({
                            code: 300,
                            errorMessage: "duplicate nickname"
                        });
                    } else if (!result) {
                        userDao.insertUser({
                            email: email,
                            password: password,
                            nickname: nickname,
                            imageUrl: "none"
                        }, function(result) {
                            if (result) {
                                res.json({
                                    code: 100
                                });
                            }
                        });
                    }
                });
            }
        });
    }
});

router.get('/update/imageUrl', function(req, res, next) {
    var email = req.query.email;
    var password = crypto.getCrypto(req.query.password);
    var imageUrl = req.query.imageUrl;

    userDao.updateUser({
        email: email,
        password: password
    }, {
        imageUrl: imageUrl
    }, function(result){
        if(result){
            res.json({
                code: 100
            });
        }else{
            res.json({
                code: 500,
                errorMessage: "server error"
            });
        }
    })
});

module.exports = router;
