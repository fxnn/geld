var Mt940Object = augment.extend(Object, {
    constructor: function(data) {
        this.data = data;
    },
});
Mt940Object.Tag = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    number: function() {
        return this.data[0];
    },
});
Mt940Object.FieldName = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    tag: function() {
        return this.data[2];
    },
    tagNumber: function() {
        return this.tag().number();
    },
});
Mt940Object.FieldDataLine = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    lineContents: function() {
        return this.data[3];
    },
});
Mt940Object.FieldData = augment.extend(Mt940Object, {
    constructor: function(lines) {
        // HINT: data is array of objects, one per line
        var data = "";
        for (var i = 0; i < lines.length; i++) {
            data += lines[i].lineContents();
        }        
        this.uber.constructor.call(this, data);
    },
    tag: function() {
        return this.data[2];
    },
    tagNumber: function() {
        return this.tag().number();
    },
});
Mt940Object.Field = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    fieldname: function() {
        return this.data[0];
    },
    fielddata: function() {
        return this.data[1].data;
    },
});
Mt940Object.Fields = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    fieldCount: function() {
        return this.data.length;
    },
});
Mt940Object.FieldsInBraces = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    tag: function() {
        return this.data[1];
    },
    tagNumber: function() {
        return this.tag().number();
    },
});
Mt940Object.Body = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    fields: function () {
        if (this.data instanceof Mt940Object.FieldsInBraces) {
            var fieldsInBraces = this.data;
            return fieldsInBraces.data[2];
        }
        return this.data;
    },
});
Mt940Object.HeaderContent = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    tag: function() {
        return this.data[0];
    },
    tagNumber: function() {
        return this.tag().number();
    },
    headerData: function() {
        return this.data[1];
    },
});
Mt940Object.HeaderBlock = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    headerContent: function() {
        return this.data;
    },
    tag: function() {
        return this.headerContent().tag();
    },
    tagNumber: function() {
        return this.tag().number();
    },
    headerData: function() {
        return this.headerContent().headerData();
    }
});
Mt940Object.Header = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    headerBlocks: function() {
        return this.data;
    }
});
Mt940Object.File = augment.extend(Mt940Object, {
    constructor: function(data) {
        this.uber.constructor.call(this, data);
    },
    body: function() {
        return this.data[1];
    }
});

var Mt940LogLevel = {
    None: {value:0, logExceptions:false, logParsingSteps:false},
    Debug: {value:1, logExceptions:true, logParsingSteps:false},
    Trace: {value:2, logExceptions:true, logParsingSteps:true}
};

// o: Operators from parser-generator.js
var Mt940Grammar = augment.extend(Object, {
    constructor: function (o, logLevel) {
        this.o = o;
        this.logLevel = logLevel;
        if (!this.logLevel) {
            this.logLevel = Mt940LogLevel.None;
        }
    },
    $token: function(name, regex) {
        this[name] = this.o.stoken(regex);
    },
    $rule: function(name, rule) {
        if (rule === null) {
            throw new Error("Rule for " + name + " is null!");
        }
        this[name] = this._process(name, rule, function(data) {
            return this._createMt940Object(name, data);
        });
    },
    _createMt940Object: function(name, data) {
        if (name == "file") return new Mt940Object.File(data);
        if (name == "body") return new Mt940Object.Body(data);
        if (name == "header") return new Mt940Object.Header(data);
        if (name == "headerblock") return new Mt940Object.HeaderBlock(data);
        if (name == "headercontent") return new Mt940Object.HeaderContent(data);
        if (name == "fields") return new Mt940Object.Fields(data);
        if (name == "fieldsInBraces") return new Mt940Object.FieldsInBraces(data);
        if (name == "field") return new Mt940Object.Field(data);
        if (name == "fieldname") return new Mt940Object.FieldName(data);
        if (name == "fielddata") return new Mt940Object.FieldData(data);
        if (name == "fielddataline") return new Mt940Object.FieldDataLine(data);
        if (name == "tag") return new Mt940Object.Tag(data);
        return new Mt940Object(data);
    },
    // Mimics parser-generator.js's process function and adds custom behaviour.
    _process: function(name, rule, processor) {
        var fn = this.o.process(rule, processor);
        fn.ruleName = name;
        return this._handleErrors(fn);
    },
    // NOTE: parser-generator.js has bad error handling, as there's no stacktrace whatsoever.
    // We add this behaviour using this decorator function.
    _handleErrors: function(fn) {
        return function() {
            try {
                if (this._isLogParsingSteps()) {
                    console.log("Parsing step", fn.ruleName);
                }
                var r =fn.apply(this, arguments);
                if (this._isLogParsingSteps()) {
                    console.log("Parsing step", fn.ruleName, "returned", r);
                }
                return r;
            } catch (ex) {
                if (this._isLogExceptions()) {
                    console.log("Parsing step", fn.ruleName, "failed with", ex);
                }
                throw ex;
            }
        }
    },
    _isLogExceptions: function() {
        return this.logLevel.logExceptions;
    },
    _isLogParsingSteps: function() {
        return this.logLevel.logParsingSteps;
    },
});
