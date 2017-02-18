var crypto = require('crypto');

exports.getCrypto = function(data) {
    if (typeof data === 'string') {
        var shasum = crypto.createHash('sha256');
        shasum.update(data);
        return shasum.digest('hex');
    } else {
		return false;
    }
};
