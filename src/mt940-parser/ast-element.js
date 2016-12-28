(function(Mt940Parser, extend){
    var AstElement = extend(Object, {
        constructor: function(data) {
            this.data = data;
        },
    });

    AstElement.Tag = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        number: function() {
            return this.data[0];
        },
    });
    AstElement.FieldName = extend(AstElement, {
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
    AstElement.FieldDataLine = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        lineContents: function() {
            return this.data[3];
        },
    });
    AstElement.FieldData = extend(AstElement, {
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
    AstElement.Field = extend(AstElement, {
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
    AstElement.Fields = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        fieldCount: function() {
            return this.data.length;
        },
    });
    AstElement.FieldsInBraces = extend(AstElement, {
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
    AstElement.Body = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        fields: function () {
            if (this.data instanceof AstElement.FieldsInBraces) {
                var fieldsInBraces = this.data;
                return fieldsInBraces.data[2];
            }
            return this.data;
        },
    });
    AstElement.HeaderContent = extend(AstElement, {
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
    AstElement.HeaderBlock = extend(AstElement, {
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
    AstElement.Header = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        headerBlocks: function() {
            return this.data;
        }
    });
    AstElement.File = extend(AstElement, {
        constructor: function(data) {
            this.uber.constructor.call(this, data);
        },
        body: function() {
            return this.data[1];
        }
    });

    Mt940Parser.AstElement = AstElement;
}(window.Mt940Parser, window.augment.extend));