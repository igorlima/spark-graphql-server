package com.mycompany.app;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.String;
import java.util.Map;
import java.util.HashMap;

class TodoData {
    static MongoClientURI uri = new MongoClientURI("mongodb://example:example@candidate.54.mongolayer.com:10775,candidate.57.mongolayer.com:10128/spark-server-with-mongo?replicaSet=set-5647f7c9cd9e2855e00007fb");
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase db = mongoClient.getDatabase("spark-server-with-mongo");
    static MongoCollection<Document> collection = db.getCollection("todos");

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

    static DataFetcher toggleFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            String id = (String) environment.getArguments().get("id");
            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
            FindIterable<Document> iterable = collection.find(
                new Document("_id", new ObjectId(id))
            );
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    document.append("completed", !Boolean.parseBoolean(document.get("completed").toString()));
                    collection.updateOne(
                        new Document("_id", new ObjectId(id)),
                        new Document("$set", document)
                    );
                    todos.add(new HashMap<String, Object>(){{
                        put("id", document.get("_id"));
                        put("title", document.get("title"));
                        put("completed", document.get("completed"));
                    }});
                }
            });
            return todos.get(0);
        }
    };

    static DataFetcher toggleAllFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            Boolean checked = (Boolean) environment.getArguments().get("checked");
            Document update = new Document().append("completed", checked);
            collection.updateMany(
                new Document("completed", new Document("$in", Arrays.asList(true, false))),
                new Document("$set", update)
            );

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

    static DataFetcher destroyFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            String id = (String) environment.getArguments().get("id");
            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
            FindIterable<Document> iterable = collection.find(
                new Document("_id", new ObjectId(id))
            );
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
            collection.deleteOne(new Document("_id", new ObjectId(id)));
            return todos.get(0);
        }
    };

    static DataFetcher clearCompletedFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
            List<ObjectId> toClear = new ArrayList<ObjectId>();
            FindIterable<Document> iterable = collection.find(
                new Document("completed", true)
            );
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    toClear.add((ObjectId) document.get("_id"));
                    todos.add(new HashMap<String, Object>(){{
                        put("id", document.get("_id"));
                        put("title", document.get("title"));
                        put("completed", document.get("completed"));
                    }});
                }
            });
            collection.deleteMany(new Document("_id", new Document("$in", toClear)));
            return todos;
        }
    };

    static DataFetcher saveFetcher = new DataFetcher() {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            String id = (String) environment.getArguments().get("id");
            String title = (String) environment.getArguments().get("title");
            List<Map<String, Object>> todos = new ArrayList<Map<String, Object>>();
            FindIterable<Document> iterable = collection.find(
                new Document("_id", new ObjectId(id))
            );
            iterable.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    document.append("title", title);
                    collection.updateOne(
                        new Document("_id", new ObjectId(id)),
                        new Document("$set", document)
                    );
                    todos.add(new HashMap<String, Object>(){{
                        put("id", document.get("_id"));
                        put("title", document.get("title"));
                        put("completed", document.get("completed"));
                    }});
                }
            });
            return todos.get(0);
        }
    };
}
