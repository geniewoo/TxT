var express = require('express');
var router = express.Router();
var gameDao = require('./Dao/gameDao');

/* GET users listing. */
router.get('/get/gameList', function(req, res, next) {
    var limitNum = Number(req.query.num);
    var skip = Number(req.query.skip) * limitNum;
    var sort = req.query.sort;
    var sortJsonArray = [{
        title: 1
    }];
    var sortJson;
    switch (sort) {
        case "gameTitle":
            sortJson = sortJsonArray[0];
            break;
        default:
            sortJson = {};
    }
    gameDao.findGames({}, {
        _id: 0,
        nickname: 0,
        title: 0
    }, sortJson, skip, limitNum, function(result) {
        if (!result) {
            res.json({
                code: 500,
                errorMessage: "server_error"
            });
        } else if (result.length > 0) {
            var gameList = [];
            for (var i in result) {
                var gameInfo = result[i];
                var gameTitle = gameInfo.TextGameInfo.gameInfo.gameTitle;
                var gameDescription = gameInfo.TextGameInfo.gameInfo.gameDescription;
                var gameImagePath = gameInfo.TextGameInfo.gameInfo.gameImagePath;
                var makerImagePath = gameInfo.TextGameInfo.maker.imagePath;
                var nickName = gameInfo.TextGameInfo.maker.nickName;
                var stars = gameInfo.TextGameInfo.playInfo.stars;
                gameList.push({
                    gameTitle: gameTitle,
                    gameDescription: gameDescription,
                    gameImagePath: gameImagePath,
                    makerImagePath: makerImagePath,
                    nickName: nickName,
                    stars: stars
                });
            }
            res.json({
                FindGameList: gameList,
                code: 100
            });
        } else {
            res.json({
                code: 200,
                errorMessage: "there is no retrieve results"
            });
        }
    });
});
router.get('/get/downloadGame', function(req, res, next) {
    var nickname = req.query.nickname;
    var title = req.query.gameTitle;
    gameDao.findGame({
        nickname: nickname,
        title: title
    }, {}, function(result) {
        if (result) {
            if (typeof result._id !== "undefined") {
                var fullGameRepo = {
                    gameInfo: result.TextGameInfo.gameInfo,
                    maker: result.TextGameInfo.maker,
                    plyInfo: result.TextGameInfo.playInfo
                };
                res.json({
                    code: 100,
                    FullGameRepo: fullGameRepo
                });
            } else {
                res.json({
                    code: 200,
                    errorMessage: "there is no game " + nickname + " : " + title
                });
            }
        } else {
            res.json({
                code: 500,
                errorMessage: "server_error"
            })
        }
    });
});
module.exports = router;
