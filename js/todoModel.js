/*jshint quotmark:false */
/*jshint white:false */
/*jshint trailing:false */
/*jshint newcap:false */
var app = app || {};

(function () {
	'use strict';

	var Utils = app.Utils;
	// Generic "model" object. You can use whatever
	// framework you want. For this application it
	// may not even be worth separating this logic
	// out, but we do this to demonstrate one way to
	// separate out parts of your application.
	app.TodoModel = function (key) {
		this.key = key;
		this.todos = [];
		this.updateTodoList();
		this.onChanges = [];
	};

	app.TodoModel.prototype.updateTodoList = function() {
		$.getJSON('https://spark-server-with-mongo.herokuapp.com/todos')
		.done(function(response, code) {
			this.todos = response || [];
			this.inform();
			$('.todoapp').loadingOverlay('remove');
		}.bind(this));
	};

	app.TodoModel.prototype.spark = function(method, path, data) {
		$('.todoapp').loadingOverlay();
		$.ajax({
			method: method,
			contentType: 'application/json',
			url: `https://spark-server-with-mongo.herokuapp.com/todos${path}`,
			data: JSON.stringify(data)
		}).done(function(response, code) {
			this.updateTodoList()
		}.bind(this));
	};

	app.TodoModel.prototype.subscribe = function (onChange) {
		this.onChanges.push(onChange);
	};

	app.TodoModel.prototype.inform = function () {
		Utils.store(this.key, this.todos);
		this.onChanges.forEach(function (cb) { cb(); });
	};

	app.TodoModel.prototype.addTodo = function (title) {
		this.spark('POST', '', {
			title: title
		});
	};

	app.TodoModel.prototype.toggleAll = function (checked) {
		this.graphql(`
			mutation {
				toggleAll (checked: ${checked}) {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.toggle = function (todoToToggle) {
		this.spark('PUT', `/${todoToToggle._id.$oid}`, {
			completed: !todoToToggle.completed
		});
	};

	app.TodoModel.prototype.destroy = function (todo) {
		this.spark('DELETE', `/${todo._id.$oid}`);
	};

	app.TodoModel.prototype.save = function (todoToSave, text) {
		this.spark('PUT', `/${todoToSave._id.$oid}`, {
			title: text
		});
	};

	app.TodoModel.prototype.clearCompleted = function () {
		this.graphql(`
			mutation {
				clearCompleted {
					id,
					title,
					completed
				}
			}
		`);
	};

})();
