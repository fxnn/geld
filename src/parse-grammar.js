var Mt940Object = function(is, data) {
    this.is = is;
    this.data = data;
}
Mt940Object.prototype = {
    assertIs: function (expected) {
        if (expected != this.is) throw "Was expected to be " + expected + ", but is " + this.is;
    },
    number: function () {
        this.assertIs("tag");
        return this.data[0];
    },
    tag: function () {
        this.assertIs("fieldname");
        return this.data[2];
    }
};

// o: Operators from parser-generator.js
var Grammar = function (o) {
    this.o = o;
};
Grammar.prototype = {
    $token: function(name, regex) {
        this[name] = this.o.stoken(regex);
    },
    $rule: function(name, rule) {
        this[name] = this.o.process(rule, function(data) {
            return new Mt940Object(name, data);
        });
    }
};
