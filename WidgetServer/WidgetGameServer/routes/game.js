var express = require('express');
var router = express.Router();
var gameDao = require('./Dao/gameDao');

/* GET users listing. */
router.get('/get/gameList', function(req, res, next) {
    console.log(req.query);
    var limitNum = Number(req.query.num);
    var skip = Number(req.query.skip) * limitNum;
    var sort = req.query.sort;
    console.log(skip, limitNum, sort);
    var sortJsonArray = [{title : 1}];
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
        if (result.length > 0) {
            var gameList = [];
            console.log(result[0].TextGameInfo);
            for (var i in result) {
                var gameInfo = result[i];
                var gameTitle = gameInfo.TextGameInfo.gameInfo.gameTitle;
                var gameDescription = gameInfo.TextGameInfo.gameInfo.gameDescription;
                var gameImagePath = gameInfo.TextGameInfo.gameInfo.gameImagePath;
                var nickName = gameInfo.TextGameInfo.maker.nickName;
                var stars = gameInfo.TextGameInfo.playInfo.stars;
                gameList.push({
                    gameTitle: gameTitle,
                    gameDescription: gameDescription,
                    gameImagePath: gameImagePath,
                    nickName: nickName,
                    stars: stars
                });
            }
            console.log(gameList);
            res.json({
                FindGameList : gameList,
                code : 100
            });
        } else {
            res.json({
                code: 200
            });
        }
    });
});
module.exports = router;
