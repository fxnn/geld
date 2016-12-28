(function(Mt940Parser, extend) {
    var AstElement = Mt940Parser.AstElement;

    var LogLevel = {
        None: {value:0, logExceptions:false, logParsingSteps:false},
        Debug: {value:1, logExceptions:true, logParsingSteps:false},
        Trace: {value:2, logExceptions:true, logParsingSteps:true}
    };

    var Grammar = extend(Object, {
        // o: Operators from parser-generator.js
        constructor: function (o, logLevel) {
            this.o = o;
            this.logLevel = logLevel;
            if (!this.logLevel) {
                this.logLevel = LogLevel.None;
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
            if (name == "file") return new AstElement.File(data);
            if (name == "body") return new AstElement.Body(data);
            if (name == "header") return new AstElement.Header(data);
            if (name == "headerBlock") return new AstElement.HeaderBlock(data);
            if (name == "headerContent") return new AstElement.HeaderContent(data);
            if (name == "fields") return new AstElement.Fields(data);
            if (name == "fieldsInBraces") return new AstElement.FieldsInBraces(data);
            if (name == "field") return new AstElement.Field(data);
            if (name == "fieldName") return new AstElement.FieldName(data);
            if (name == "fieldData") return new AstElement.FieldData(data);
            if (name == "fieldDataLine") return new AstElement.FieldDataLine(data);
            if (name == "tag") return new AstElement.Tag(data);
            return new AstElement(data);
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

    Mt940Parser.LogLevel = LogLevel;
    Mt940Parser.Grammar = Grammar;
}(window.Mt940Parser, window.augment.extend));