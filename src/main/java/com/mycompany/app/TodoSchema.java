package com.mycompany.app;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import static graphql.Scalars.GraphQLString;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.Map;
import java.util.HashMap;

public class TodoSchema {
    static MongoClientURI uri = new MongoClientURI("mongodb://example:example@candidate.54.mongolayer.com:10775,candidate.57.mongolayer.com:10128/spark-server-with-mongo?replicaSet=set-5647f7c9cd9e2855e00007fb");
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase db = mongoClient.getDatabase("spark-server-with-mongo");
    static MongoCollection<Document> collection = db.getCollection("todos");

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

    static DataFetcher todoFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
            FindIterable<Document> iterable = collection.find();
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    todos.add(new HashMap<String, Object>(){{
                        put("id", document.get("_id"));
                        put("title", document.get("title"));
                        put("completed", document.get("completed"));
                    }});
                }
            });
            return todos;
        }
    };

    static DataFetcher addFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            Object title = environment.getArguments().get("title");
            Document newTodo = new Document()
                .append("title", title)
                .append("completed", false);
            collection.insertOne(newTodo);

            Map<String, Object> todo = new HashMap<String, Object>(){{
                put("id", newTodo.get("_id"));
                put("title", newTodo.get("title"));
                put("completed", newTodo.get("completed"));
            }};
            return todo;
        }
    };

    static GraphQLObjectType queryType = newObject()
        .name("Query")
        .field(newFieldDefinition()
            .type(new GraphQLList(todoType))
            .name("todos")
            .description("todo tasks")
            .dataFetcher(todoFetcher)
            .build())
        .build();

    static GraphQLObjectType mutationType = newObject()
        .name("Mutation")
        .field(newFieldDefinition()
            .type(todoType)
            .name("add")
            .description("add a todo")
            .argument(newArgument()
                .name("title")
                .description("todo title")
                .type(new GraphQLNonNull(GraphQLString))
                .build())
            .dataFetcher(addFetcher)
            .build())
        .field(newFieldDefinition()
            .type(todoType)
            .name("toggle").build())
        .field(newFieldDefinition()
            .type(todoType)
            .name("toggleAll").build())
        .field(newFieldDefinition()
            .type(todoType)
            .name("destroy").build())
        .field(newFieldDefinition()
            .type(todoType)
            .name("clearCompleted").build())
        .field(newFieldDefinition()
            .type(todoType)
            .name("save").build())
        .build();


    public static GraphQLSchema schema = GraphQLSchema.newSchema()
        .query(queryType)
        .mutation(mutationType)
        .build();
}
