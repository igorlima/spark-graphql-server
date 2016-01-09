package com.mycompany.app;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import static graphql.Scalars.GraphQLString;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public class TodoSchema {

    public static GraphQLObjectType todoType = newObject()
        .name("todoType")
        .field(newFieldDefinition()
            .type(GraphQLString)
            .name("id")
            .description("Todo id")
            .build())
        .field(newFieldDefinition()
            .type(GraphQLString)
            .name("title")
            .description("Task title")
            .build())
        .field(newFieldDefinition()
            .type(GraphQLBoolean)
            .name("completed")
            .description("Flag to mark if the task is completed")
            .build())
        .build();

    static GraphQLObjectType queryType = newObject()
        .name("Todo")
        .field(newFieldDefinition()
            .type(new GraphQLList(todoType))
            .name("todos")
            .description("todo tasks")
            .dataFetcher(TodoData.todoFetcher)
            .build())
        .build();

    static GraphQLObjectType mutationType = newObject()
        .name("Todo")
        .field(newFieldDefinition()
            .type(todoType)
            .name("add")
            .description("add a todo")
            .argument(newArgument()
                .name("title")
                .description("todo title")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .dataFetcher(TodoData.addFetcher)
            .build())

        .field(newFieldDefinition()
            .type(todoType)
            .name("toggle")
            .description("toggle the todo")
            .argument(newArgument()
                .name("id")
                .description("todo id")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .dataFetcher(TodoData.toggleFetcher)
            .build())

        .field(newFieldDefinition()
            .type(new GraphQLList(todoType))
            .name("toggleAll")
            .description("toggle all todos")
            .argument(newArgument()
                .name("checked")
                .description("checked flag")
                .type(new GraphQLNonNull(GraphQLBoolean))
                .build())
            .dataFetcher(TodoData.toggleAllFetcher)
            .build())

        .field(newFieldDefinition()
            .type(todoType)
            .name("destroy")
            .description("destroy the todo")
            .argument(newArgument()
                .name("id")
                .description("todo id")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .dataFetcher(TodoData.destroyFetcher)
            .build())

        .field(newFieldDefinition()
            .type(new GraphQLList(todoType))
            .name("clearCompleted")
            .description("clear all completed todos")
            .dataFetcher(TodoData.clearCompletedFetcher)
            .build())

        .field(newFieldDefinition()
            .type(todoType)
            .name("save")
            .description("edit a todo")
            .argument(newArgument()
                .name("id")
                .description("todo id")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .argument(newArgument()
                .name("title")
                .description("todo title")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .dataFetcher(TodoData.saveFetcher)
            .build())

        .build();


    public static GraphQLSchema schema = GraphQLSchema.newSchema()
        .query(queryType)
        .mutation(mutationType)
        .build();
}
