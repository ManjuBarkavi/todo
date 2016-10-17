
$(function(){

  // Todo Model
  // ----------

	var preferences = Backbone.View.extend({
		
		defaults : function(){
						return {
							appMode : 0, //0 = normal, 1 = scrum
						};
					}
		
	});
	
	var userPrefer = new preferences();
	
  // Our basic **Todo** model has `title`, `order`, and `done` attributes.
  var Todo = Backbone.Model.extend({

	  url : function(){
		  	
		  	if(awApp.loggedinUser)
		  		return "/todo?contactKey="+awApp.loggedinUser.id;
		  	else
		  		return "/todo";
	  },
    // Default attributes for the todo item.
    defaults: function() {
      return {
        title: "empty todo...",
        order: Todos.nextOrder(),
        type: 0, // 0 = normal, 1 = scrum 
        score:0,
        isDone: false,
        status: "active"
      };
    },

    // Ensure that each todo created has `title`.
    initialize: function() {
      if (!this.get("title")) {
        this.set({"title": this.defaults().title});
      }
    },

    // Toggle the `done` state of this todo item.
    toggle: function() {
      this.save({isDone: !this.get("isDone")});
    }

  });

  // Todo Collection
  // ---------------

  var TodoList = Backbone.Collection.extend({

    // Reference to this collection's model.
    model: Todo,

    // Save all of the todo items under the `"todos-backbone"` namespace.
    //localStorage: new Backbone.LocalStorage("todos-backbone"),
    url: function() {
    	if(awApp.loggedinUser){
    		return "/todo?contactKey="+awApp.loggedinUser.id;
    		
    	} else {
    		return "/todo";
    	}
    },

    // Filter down the list of all todo items that are finished.
    done: function() {
      return this.filter(function(todo){ return todo.get('isDone'); });
    },

    // Filter down the list to only todo items that are still not finished.
    remaining: function() {
      return this.without.apply(this, this.done());
    },

    // We keep the Todos in sequential order, despite being saved by unordered
    // GUID in the database. This generates the next order number for new items.
    nextOrder: function() {
      if (this.length == 0) return 1;
      return this.last().get('order') + 1;
    },

    // Todos are sorted by their original insertion order.
    comparator: function(todo) {
      return todo.get('order');
    }

  });

  // Create our global collection of **Todos**.
  var Todos = new TodoList();

  // Todo Item View
  // --------------

  // The DOM element for a todo item...
  var TodoView = Backbone.View.extend({

    //... is a list tag.
    tagName:  "li",

    // Cache the template function for a single item.
    template: _.template($('#item-template').html()),

    // The DOM events specific to an item.
    events: {
      "click .toggle"   : "toggleDone",
      "dblclick .view"  : "edit",
      "click a.destroy" : "clear",
      "click .share"	: "shareTaskinFeed",
      "keypress .edit"  : "updateOnEnter",
      "blur .edit"      : "close"
    },

    
    initialize: function() {
      this.listenTo(this.model, 'change', this.render);
      this.listenTo(this.model, 'destroy', this.remove);
    },

    // Re-render the titles of the todo item.
    render: function() {
      this.$el.html(this.template(this.model.toJSON()));
      this.$el.toggleClass('done', this.model.get('isDone'));
      this.input = this.$('.edit');
      return this;
    },

    // Toggle the `"done"` state of the model.
    toggleDone: function() {
      this.model.toggle();
    },

    // Switch this view into `"editing"` mode, displaying the input field.
    edit: function() {
      this.$el.addClass("editing");
      var ipVal = this.input.val();
      this.input.val(ipVal);
      this.input.focus();
    },

    // Close the `"editing"` mode, saving changes to the todo.
    close: function() {
      var value = this.input.val();
      if (!value) {
        this.clear();
      } else {
        this.model.save({title: value});
        this.$el.removeClass("editing");
      }
    },

    // If you hit `enter`, we're through editing the item.
    updateOnEnter: function(e) {
      if (e.keyCode == 13) this.close();
    },

    // Remove the item, destroy the model.
    clear: function(e) {
    	
    	e.stopPropagation();
    	var data = this.model;
    	this.model.destroy({data: JSON.stringify(data), contentType: 'application/json'});
      //return;
    },
    
    shareTaskinFeed : function(e)
  						{
    						e.stopPropagation();
    						Share.shareAsFeed(this.model.get("title"));
  						}

  });

  
 var ShareView = Backbone.View.extend({
	  
	  shareAsFeed	:	function(feed)
	  					{
		  					if(awApp.loggedinUser)
		  					{
		  					
		  					$.ajax({
		  						
		  						url:"/sendFeed?contactKey="+awApp.loggedinUser.id,
		  						type:"POST",
		  						data: feed,
		  						contentType:"application/json",
		  						success : function(data)
		  						{
		  							Share.showMessage("Feed Posted!");
		  						},
		  						error : function(data)
		  						{
		  							console.log(data);
		  						}
		  					});
		  					}	
		  					
	  					},
	  	showMessage	:	function(message)
	  					{
	  						$("#voicebox").text(message).fadeIn();
	  						setTimeout(function(){$("#voicebox").fadeOut();}, 2000);
	  					},
	  	hideMessage	:	function(message)
	  					{
	  						$("#voicebox").hide();
	  					}
  });
  
  var Share = new ShareView;
  
  var chart = Backbone.View.extend({
	  
	  
	  
		generateChart : function()	{	
			
							
							console.log(Todos);
							
							var total = Todos.pluck("score");
							var totalEffort = 0;
							for(var j=0; j< total.length; j++)
							{
								totalEffort = totalEffort +total[j];
							}
							
							var done = Todos.done();
							var scores = []
							for(var i=0; i< done.length; i++)
							{
								var model = done[i].toJSON();
								scores[i] =  model.score;
							}	
							
							
							var sprintDuration = 7;
							var idealLine = [ [0,totalEffort],[sprintDuration,0] ];
							
							
							
							var burnLine = [[0,totalEffort]];
							var subTotal = 0;
							for(var k=1; k<=sprintDuration; k++)
							{
								var h = scores[k-1];
								if(h)
									burnLine[k] = [k,(totalEffort-scores[k-1]-subTotal)];
								
								subTotal += scores[k-1];
								
							}	
							//return;
							
							zingchart.render({
								    id: 'burnDownChart',
								    data: {
								    	
								      type: 'line',
								      "title": {
								    	  "text":"Burn Down Chart"
								    	},
							
								      legend: {
								    	    "header": {
								    	      "text": "Legend"
								    	    },
								    	    "draggable": true,
								    	    "drag-handler": "icon"
								    	  },
								    		  
								    		  "scale-y":{
								    			    "values":"0:"+totalEffort+":1",
								    			    "label": {
										    		      "text": "Total Velocity"
										    		    }
								    		  },
								    		  
								    		  "scale-x":{
								    			    "values":"0:"+sprintDuration+":1",
								    			    "label": {
										    		      "text": "Sprint Duration (Days)"
										    		    }
								    		  },
								    		  
								           series: [{
												        values: burnLine,
												        "text":"Burned"
											      	}, {
												        values: idealLine,
												        "text":"Ideal"
											      }]
									      
								    }
								  });
							
							$("#burnDownChart-graph-id0-legend-item_0 tspan").text("Burned");
							$("#burnDownChart-graph-id0-legend-item_1 tspan").text("Ideal");
							
							this.showChart();
						},
		showChart	 :	function() {
							$("#popup").html( $("#burnDownChart") );
							$("#popupcontainer").show();
							$("#burnDownChart").append($("#shareChart"));
							$("#shareChart").show();
							$("#burnDownChart").append($("#printChart"));
							$("#printChart").show();
							$("#burnDownChart").append($("#saveChart"));
							//$("#saveChart").show();
						},
					
  });		

  var chartView = new chart;
    
  var Menu = Backbone.View.extend({
	  
		  el		:	$("#menucontainer"),
		  template	:   $("#menu-template").html(),
		  events	:	{
			  				"click #menu-close"		: "closeMenuBar",
			  				"click #burndownlist"	: "generateChart",
			  				"click .signin"			: "signin"
			  				
		  				},
		  render	:	function()
		  				{
			  				var template = _.template(this.template, {});
			  				this.$el.html(template);
			  				this.$el.show();
		  				},
	closeMenuBar	:	function(e)
		  				{
		  					this.$el.hide();
		  				},
	generateChart	:	function()
						{
							chartView.generateChart();
						},
		signin		:	function()
						{
							$.ajax({
								
								//url: "https://access.anywhereworks.com/o/oauth2/auth?response_type=code&client_id=29354-03ca85f7a4dd032e86c3c0842f67a418&scope=awapis.notifications.write%20awapis.chat.streams.push%20awapis.streams.read%20awapis.users.read%20awapis.feeds.write%20awapis.identity%20awapis.account.read%20&redirect_uri=http://todo-scrum-live.appspot.com/oauth/callback&approval_prompt=force&access_type=offline&state="+awApp.loggedinUser.id,
								url: "/getCode?awContactKey="+awApp.loggedinUser.id,
								type: "GET",
								success : function(data)
											{
												if(data == "success")
												{
													Share.showMessage("Signed in successfully");
												} else {
													Share.showMessage("Something went wrong!");
												}
											},
								error	:	function()
											{
													Share.showMessage("Something went wrong!");
											}
								
							});
						}
		
  });
  
  var MenuView = new Menu();
  
  
  // The Application
  // ---------------


  var AppView = Backbone.View.extend({

    el: $("#todoapp"),

    statsTemplate: _.template($('#stats-template').html()),

    // Delegated events for creating new items, and clearing completed ones.
    events: {
    	"keypress #new-todo"	:	"createOnEnter",
    	"click #clear-completed"	:	"clearCompleted",
    	"click #toggle-all"	:	"toggleAllComplete",
    	"click #menu"	:	"showMenu",
    	"click #menucontainer"	:	"closeMenu",
    	"click #popupcontainer-close" :	"closePopup",
    	"click #shareChart"	:	"getBase64Image",
    	"click #preferences"	:	"showPreferences",
    	"click .activate"	:	"switchScrumMode",
    	"click .deactivate"	:	"switchNormalMode",
    	"click #staffHours_s_c358_mo li a"	:	"getScrumDuration",
    	"click #printChart"	:	"printChart",
    	"click #saveChart"	:	"saveChart"
    	},

   
    initialize: function() {

      this.input = this.$("#new-todo");
      this.allCheckbox = this.$("#toggle-all")[0];

     // this.listenTo(Todos, 'add', this.addOne);
      this.listenTo(Todos, 'reset', this.addAll);
      this.listenTo(Todos, 'all', this.render);

      this.footer = this.$('footer');
      this.main = $('#main');
      
      $("#new-todo").focus();
      var ajaxcall = Todos.fetch();
      ajaxcall.abort();
      
      var _this = this;
      setTimeout(function(){ 
	      
	
	      var url = awApp.loggedinUser ? "/todo?contactKey="+awApp.loggedinUser.id : "/todo";
	      $.ajax({
		      url: url,
		      type:"GET",
		      success:function(data){
		    	  //data = JSON.stringify([{title:"one",score:10,isDone:true},{title:"two",score:10,isDone:true},{title:"three",score:10,isDone:true},{title:"four",score:10,isDone:true},{title:"five",score:10,isDone:true},{title:"six",score:10,isDone:true},{title:"seven",score:10,isDone:true}]);
		    	  if(data.length > 0){
				      data = JSON.parse(data);
				      Todos = new TodoList(data);
				      _this.addAll();
				      _this.render();
		    	  }
		      }
	      });
	
	
	      console.log(Todos);
     },300);
      
    },

    // Re-rendering the App just means refreshing the statistics -- the rest
    // of the app doesn't change.
    render: function() {
    	
    	if(Todos.length > 0) {
	    	if(userPrefer.appMode)
	        {
	      	  //Todos.models = Todos.where({"type" : 1});
	    	  //Todos = new TodoList(Todos.where({"type" : 1}));
	      	  
	        }	else {
	        	//Todos.models = Todos.where({"type" : 0});
	        	//Todos = Todos.where({"type" : 0});
	        	//Todos = new TodoList(Todos.where({"type" : 0}));
	        	
	        }
	    }
    	
    	
      var done = Todos.done().length;
      var remaining = Todos.remaining().length;

      
      if (Todos.length) {
        this.main.show();
        this.footer.show();
        this.footer.html(this.statsTemplate({done: done, remaining: remaining}));
        
        if(remaining > 0 && awApp.loggedinUser)
        {	
	        awApp.postMessage( 'showCount', { 
			    'count': remaining,
			    'id' : awApp.loggedinUser.id
			});
        }
        
        this.hideRandomMessage();
      } else {
        this.main.hide();
        this.footer.hide();
        this.showRandomMessage();
      }

      this.allCheckbox.checked = !remaining;
      
      if(userPrefer.appMode)
    	 {
    	  	this.showScore();
    	 } 
      
    },

    showRandomMessage	:	function()
    						{
    							var msg = ["Hurray! you are all done", "OMG! you are great", "You are awesome!"];
    							var num = Math.floor(Math.random() * msg.length);
    							$("#randomMsg").text(msg[num]).show();
    						},
    						
    hideRandomMessage	:	function()
    						{
    							$("#randomMsg").hide();
    						},
    // Add a single todo item to the list by creating a view for it, and
    // appending its element to the `<ul>`.
    addOne: function(todo) {
    	if(userPrefer.appMode)
    	{
    		todo.set("score",parseInt($("#selectScore").val()));
    	}	
      var view = new TodoView({model: todo});
      this.$("#todo-list").append(view.render().el);
    },

    // Add all items in the **Todos** collection at once.
    addAll: function() {
      Todos.each(this.addOne, this);
    },

    createOnEnter: function(e) {
      if (e.keyCode != 13) return;
      if (!this.input.val()) return;
      
      var tempTodo = {title: this.input.val()};
      
      if(userPrefer.appMode)
	  	{
    	  tempTodo.score = parseInt($("#selectScore").val());
	  	}
      
      var todo = Todos.create(tempTodo);
      this.input.val('');
      var view = new TodoView({model: todo});
      this.$("#todo-list").append(view.render().el);
      
      $("#randomMsg").hide();
      $("#main").show();
      
    },

    // Clear all done todo items, destroying their models.
    clearCompleted: function() {
      _.invoke(Todos.done(), 'destroy');
      return false;
    },

    toggleAllComplete: function () {
      var done = this.allCheckbox.checked;
      Todos.each(function (todo) { todo.save({'isDone': done}); });
    },
    
    showMenu	:	function() {
    	MenuView.render();
    },
    
    closeMenu	:	function() {
    	MenuView.closeMenuBar();
    },
    
    switchScrumMode	:	function(e) {
    	
    	$("#activateScrumMode").removeClass("activate");
    	$("#deactivateScrumMode").show();
    	$("#activateScrumMode").hide();
    	$("#deactivateScrumMode").addClass("deactivate");
    	
    	if(!userPrefer.appMode)
    	{
    		
    		userPrefer.appMode = 1;
    		Share.showMessage("Activated Scrum Mode");
    		this.showScore();
    		
    	}
    },
    
    switchNormalMode	:	function(e)
    						{
						    	$("#deactivateScrumMode").removeClass("deactivate");
						    	$("#deactivateScrumMode").hide();
						    	$("#activateScrumMode").show();
						    	$("#activateScrumMode").addClass("activate");

						    	if(userPrefer.appMode)
						    	{
						    		//this.deactivateScrumMode();
						    		userPrefer.appMode = 0;
						    		Share.showMessage("Activated Normal Mode");
						    		this.hideScore();
						    	}
						    	
						    	
    						},
    
    showPreferences		:	function()
						    {
    							var template = _.template( $( "#scrumViewTemplate" ).html());
						    	$("#popup").html( template );
						    	$("#popupcontainer").show();
						    },
						    
    getScrumDuration	:	function(e)
						    {
						    	var numOfDays = $(e.currentTarget).html();
						    	var sprintDuration = $(".dropdown-toggle").html(numOfDays+"<span class='caret'style='margin-left:10px'></span>");
						    },    
    
    showScore	:	function()
    				{
    					$("#new-todo").addClass("scrumMode");
					  	$("#selectScore").show();
					  	
    				},
    hideScore	:	function()
    				{
					  	$("#new-todo").removeClass("scrumMode");
					  	$("#selectScore").hide();
    				},
    				
    closePopup	:	function()
				    {
				    	$("#popupcontainer").hide();
				    },
				    
getBase64Image	:	function()
				    {
				    	zingchart.EXPORTURL = 'http://localhost:8888/uploadLanguage'; // this will work only when the example will run under my.server.com
				    	zingchart.AJAXEXPORT = true;
				    	zingchart.exec('burnDownChart', 'getimagedata', {
				    	    format : 'png',
				    	    callback : function(sImageData) {
				    	        $('#burnDownChart').append('<img src="' + sImageData + '">');
				    	    }
				    	});
				    },
				    
	printChart	:	function() {
						zingchart.exec('burnDownChart', 'print');
					},
	saveChart	:	function() {
						zingchart.exec('burnDownChart', 'saveasimage');
					}	
				
	
  });

  // Finally, we kick things off by creating the **App**.
  var App = new AppView;
  
  
 
});
