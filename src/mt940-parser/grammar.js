(function(module, extend) {
    var LogLevel = {
        None: {value:0, logExceptions:false, logParsingSteps:false},
        Debug: {value:1, logExceptions:true, logParsingSteps:false},
        Trace: {value:2, logExceptions:true, logParsingSteps:true}
    };

    var Grammar = extend(Object, {
        /**
         * @param {Object} o is the Operators object from parser-generator.js
         * @param {Function} astElementFactory is a function (String, String) -> Object, transferring an AST 
         *       element by its name and its contents to the object to be returned
         * @param {Object} logLevel denotes the verbosity regarding debug statements.
         */
        constructor: function (o, astElementFactory, logLevel) {
            this.o = o;
            this.astElementFactory = astElementFactory;
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
                return this._createAstElement(name, data);
            });
        },
        _createAstElement: function(name, data) {
            return this.astElementFactory(name, data);
        },
        /** Mimics parser-generator.js's process function and adds custom behaviour. */
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

    module.LogLevel = LogLevel;
    module.Grammar = Grammar;
}(window.Mt940Parser, window.augment.extend));