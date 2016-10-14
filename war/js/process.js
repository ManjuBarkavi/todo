var TodoApp = (function(todo){
	 var _app = _context = _appUser = {};
	 todo.push = function(type,obj){
		 $.extend(obj,{'id':_context.id});
		 _app.postMessage(type,obj);
	 };
	 init = function(){
		 _app = window.AAFClient.init();
		 _app.on('registered', function(data) {
		   console.error("On app.registered event.",data);
		   _appUser = data.user;
		 });
		 _app.on('activated',function(data){
		  console.error("On app activation.",data);
		  _context = data.context;
		 });
		 _app.on('context-change',function(data){
		  console.error("On context change.",data);
		  _context = data.context;
		 });
		 _app.on('deactivated',function(data){
		 console.error("On app deactivation.",data);
		 });
	}();
	return todo;
})({});