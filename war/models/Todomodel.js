var app = app || {};

(function($){
	
	app.TodoModel = Backbone.Model.extend({
		
		url 		:	"/getTodo",
		defaults	: function() {
						      return {
						        title: "new todo",
						        order: app.TodoCollection.nextOrder(),
						        done: false
						      };
		    			},
		    toggle	: function() {
		    			      this.save({done: !this.get("done")});
		    			    }

		});
		
})(jQuery);