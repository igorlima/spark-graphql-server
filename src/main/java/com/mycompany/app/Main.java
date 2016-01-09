package com.mycompany.app;

import static spark.Spark.*;

import graphql.GraphQL;
import com.mongodb.util.JSON;
import java.lang.String;

public class Main {

    public static void main(String[] args) {
        String ENV_PORT = System.getenv().get("PORT");
        port( ENV_PORT == null ? 4567 : Integer.parseInt(ENV_PORT) );

        /*
         * enable CORS in our Spark server. CORS is the acronym for “Cross-origin resource sharing”: a mechanism that allows to access REST resources outside the original domain of the request.
         * http://www.mastertheboss.com/cool-stuff/create-a-rest-services-layer-with-spark
         */
        options("/*", (request,response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request,response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        post("/graphql", (request, response) -> {
            GraphQL graphql = new GraphQL(TodoSchema.schema);
            response.type("application/json");
            return JSON.serialize( graphql.execute(request.body()).getData() );
        });

    }
}
