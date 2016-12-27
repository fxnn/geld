var Mt940Object = function(is, data) {
    this.is = is;
    this.data = data;
}
Mt940Object.prototype = {
    assertIs: function () {
        var expected = Array.prototype.slice.call(arguments);
        if (expected.indexOf(this.is) < 0) {
            throw new Error("Was expected to be " + expected + ", but is " + this.is);
        }
    },
    tag: function () {
        this.assertIs("fieldname", "fieldsInBraces", "headerblock");
        if (this.is == "fieldname") {
            return this.data[2].data[0];
        }
        if (this.is == "fieldsInBraces") {
            return this.data[1];
        }
        return this.data[0].data[0];
    },
    headerdata: function() {
        this.assertIs("headerblock");
        return this.data[1];
    },
    body: function () {
        this.assertIs("file");
        return this.data[1];
    },
    fieldname: function() {
        this.assertIs("field");
        return this.data[0];
    },
    fielddata: function() {
        this.assertIs("field");
        return this.data[1].data;
    },
    fields: function () {
        this.assertIs("body");
        if (this.data.is == "fieldsInBraces") {
            var fieldsInBraces = this.data;
            return fieldsInBraces.data[2];
        }
        return this.data;
    },
    fieldCount: function() {
        this.assertIs("fields");
        return this.data.length;
    },
};

// o: Operators from parser-generator.js
var Mt940Grammar = function (o) {
    this.o = o;
};
Mt940Grammar.prototype = {
    // flatten(a) flattens an array a of arrays
    // http://stackoverflow.com/a/16953805
    _flatten: function(a, r){
        if(!r){ r = []}
        for(var i=0; i<a.length; i++){
            if(a[i].constructor == Array){
                this._flatten(a[i], r);
            }else{
                r.push(a[i]);
            }
        }
        return r;
    },
    // concatToString(a) concats all elements in array a to one string
    _concatToString: function(a) {
        var result = "";
        for(var i = 0; i <a.length; i++) {
            if (a[i] == null) {
                continue;
            }
            if (Object.keys(a[i]).indexOf("data") >= 0) {
                result = result + a[i].data;
            } else {
                result = result + a[i];
            }
        }
        return result;
    },
    // NOTE: parser-generator.js has bad error handling, as there's no stacktrace whatsoever.
    // We add this behaviour using this decorator function.
    _handleErrors: function(fn) {
        return function() {
            try {
                console.log("Parsing step", fn.ruleName);
                return fn.apply(this, arguments);
            } catch (ex) {
                console.log("Parsing step", fn.ruleName, "failed with", ex);
                throw ex;
            }
        }
    },
    // Mimics parser-generator.js's process function and adds custom behaviour.
    _process: function(name, rule, processor) {
        var fn = this.o.process(rule, processor);
        fn.ruleName = name;
        return this._handleErrors(fn);
    },
    $token: function(name, regex) {
        this[name] = this.o.stoken(regex);
    },
    $rule: function(name, rule) {
        if (rule === null) {
            throw new Error("Rule for " + name + " is null!");
        }
        this[name] = this._process(name, rule, function(data) {
            return new Mt940Object(name, data);
        });
    },
    $inline: function(name, rule) {
        this[name] = this._process(name, rule, function(inlined) {
            if (Object.keys(inlined).indexOf("data") >= 0) {
                return new Mt940Object(name, inlined.data);
            }
            throw new Error("Cannot inline " + JSON.stringify(inlined));
        });
    },
    $concat: function(name, rule) {
        var _concatToString = this._concatToString;
        this[name] = this._process(name, rule, function(a) {
            return new Mt940Object(name, _concatToString(a));
        });
    }
};
