/*
  ###AAFClient API
 */


 'use strict';
var Utils = (function(utils){
  var _decode = function(s){
    return decodeURIComponent((s || '').replace( /\+/g, " " ));
  };

  utils.queryParameters = function(queryString){
    var result = {},
      keyValuePairs,
      keyAndValue,
      key,
      value;

    queryString = queryString ||
      ( document.location.search || '' ).slice(1);

    if (queryString.length === 0) { return result; }

    keyValuePairs = queryString.split('&');

    for (var i = 0; i < keyValuePairs.length; i++) {
      keyAndValue = keyValuePairs[i].split('=');
      key   = _decode(keyAndValue[0]);
      value = _decode(keyAndValue[1]) || '';
      result[key] = value;
    }
    return result;
  };
  return utils;
})({});


  var PROMISE_TIMEOUT = 5000, // 5 seconds
    AAF_EVENT       = /^aaf\./,
    Promise         = window.Promise,
    version         = "1.0",
    pendingPromises = {},
    promiseCount    = 0,
    requestCount    = 0;

  function rawPostMessage(client,msg,forceReady){
    client._source.postMessage(msg,client._origin);
    // if(client.ready || forceReady){
    //   client._source.postMessage(msg,client._origin);
    // }else{
    //   client.on('app.registered',rawPostMessage.bind(null,client,msg));
    // }

  }

  function isValidEvent(client, event) {
    return client._origin === event.origin && client._source === event.source;
  }

  function messageHandler(client, event) {

//   if (!isValidEvent(client, event)) { return; }
      var data = event.data;
      if (!data) { return; }
      if (typeof data === 'string') {
        try {
          data = JSON.parse(event.data);
        } catch (e) {
          return;
        }
      }
      var pendingPromise;
      if (data.id && (pendingPromise = pendingPromises[data.id])) {
        if (data.error) {
            var err = data.error;
            if (err.code) {
              err = new Error(data.error.msg);
              err.name = data.error.code;
              err.stack = data.error.stack;
            }
            pendingPromise.reject(err);
          } else {
            pendingPromise.resolve(data.result);
          }
        } else if (AAF_EVENT.test(data.key)) {
          var key = data.key.replace(AAF_EVENT, '');
          if (client) {
            client.trigger(key, data.message);
          }
        }

    }
  var Client = function(origin,appId){
    this._origin = origin;
    this._source = window.parent;
    this._appId= appId;
    this._messageHandlers = {};
    this._metadata = null;
    this._context = null;
    this.ready = false;

    this.on('app.registered',function(data){
      // console.debug("Triggering app.registered event.");
      this.ready = true;
      this._metadata = data.metadata;
      this._context = data.context;
    });

    this.on('context.updated',function(context){
      this._context = context;
    },this);

    this.postMessage('handshake',{version:version});
    window.addEventListener('message',messageHandler.bind(null,this));
  };
  Client.prototype = {

    /**
     * [Allows you to send message events to the AnywhereWorks App.]
     * @param  {[String]} name [Name of the message event. This determines the name of the iframe event your app will receive. For example, if you set this to 'hello', Your app will receive the event 'iframe.hello']
     * @param  {[Object]} data [a JSON object with any data that you want to pass along with the event]
     * @Example :
         var client = AAFClient.init()
         client.postMessage('hello',{awesome : true});
     */
      postMessage : function(name,data){
      var msg = JSON.stringify({key:"iframe."+name,message:data,appId:this._appId});
      rawPostMessage(this,msg,name === "iframe.handshake");
    },
    /**
     * [function description]
     * @param  {[type]} name    [description]
     * @param  {[type]} handler [description]
     * @param  {[type]} context [description]
     * @return {[type]}         [description]
     */
    on : function(name,handler,context){
      if(typeof handler == 'function'){
        this._messageHandlers[name] = this._messageHandlers[name] || [];
        this._messageHandlers[name].push(context ? handler.bind(context):handler);
      }
    },
    off: function(name, handler) {
      if (!this._messageHandlers[name]) { return false; }
      var index = this._messageHandlers[name].indexOf(handler);
      return this._messageHandlers[name].splice(index, 1)[0];
    },


    has: function(name, handler) {
      if (!this._messageHandlers[name]) { return false; }
      return this._messageHandlers[name].indexOf(handler) !== -1;
    },


    trigger: function(name, data) {
      if (!this._messageHandlers[name]) { return false; }
      this._messageHandlers[name].forEach(function(handler) {
        handler(data);
      });
    },

    request: function(options) {
      var requestKey = 'request:' + requestCount++,
          deferred = Promise.defer();

      if (typeof options === 'string') {
        options = { url: options };
      }

      this.on(requestKey + '.done', function(evt) {
        deferred.resolve.apply(this, evt.responseArgs);
      });

      this.on(requestKey + '.fail', function(evt) {
        deferred.reject.apply(this, evt.responseArgs);
      });

      this.postMessage(requestKey, options);

      return deferred.promise;
    },

    metadata: function() {
      var deferred = Promise.defer();
      if (this._metadata) {
        deferred.resolve(this._metadata);
      } else {
        this.on('app.registered', function() {
          deferred.resolve(this._metadata);
        }.bind(this));
      }
      return deferred.promise;
    },

    context: function() {
      var deferred = Promise.defer();
      if (this._context) {
        deferred.resolve(this._context);
      } else {
        this.on('app.registered', function() {
          deferred.resolve(this._context);
        }.bind(this));
      }
      return deferred.promise;
    },

    get: function(path) {
      var paths = Array.isArray(path) ? path : [path];
      return wrappedPostMessage.call(this, 'get', paths);
    },

    set: function(key, val) {
      var obj = key;
      if (typeof key === 'string') {
        obj = {};
        obj[key] = val;
      }
      return wrappedPostMessage.call(this, 'set', obj);
    },

    invoke: function() {
      return wrappedPostMessage.bind(this, 'invoke').apply(null, arguments);
    }
  };

  var originUrl = function(location){
    return location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
  };

  var AAFClient = {};
  AAFClient.init = function(callback){
    var params = Utils.queryParameters(),
        client;
      // if(params.origin || !params.app_guid) {return false;}
      // if(params.origin) {return false;}
      // params.origin = originUrl(window.parent.location);
      params.origin = "*";
      client = new Client(params.origin,params.app_guid);

      if(typeof callback === "function"){
        client.on('app.registered',callback.bind(client));
      }
      return client;
  };
