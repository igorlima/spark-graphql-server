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
		$('.todoapp').loadingOverlay();
		$.ajax({
			method: 'POST',
			contentType: 'application/graphql',
			url: 'https://spark-graphql-server.herokuapp.com/graphql',
			data: 'query Todo { todos { id, title, completed } }'
		}).done(function(response, code) {
			this.todos = response['todos'] || [];
			this.inform();
			$('.todoapp').loadingOverlay('remove');
		}.bind(this));
	};

	app.TodoModel.prototype.graphql = function(query) {
		$('.todoapp').loadingOverlay();
		$.ajax({
			method: 'POST',
			contentType: 'application/graphql',
			url: 'https://spark-graphql-server.herokuapp.com/graphql',
			data: query
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
		this.graphql(`
			mutation Todo {
				add (title: "${title}") {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.toggleAll = function (checked) {
		this.graphql(`
			mutation Todo {
				toggleAll (checked: ${checked}) {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.toggle = function (todoToToggle) {
		this.graphql(`
			mutation Todo {
				toggle (id: "${todoToToggle.id}") {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.destroy = function (todo) {
		this.graphql(`
			mutation Todo {
				destroy (id: "${todo.id}") {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.save = function (todoToSave, text) {
		this.graphql(`
			mutation Todo {
				save (id: "${todoToSave.id}", title: "${text}") {
					id,
					title,
					completed
				}
			}
		`);
	};

	app.TodoModel.prototype.clearCompleted = function () {
		this.graphql(`
			mutation Todo {
				clearCompleted {
					id,
					title,
					completed
				}
			}
		`);
	};

})();
