package com.distribuida;

import com.distribuida.db.Persona;
import com.distribuida.servicios.ServicioPersona;
import com.google.gson.Gson;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;

import static spark.Spark.*;

public class Principal {
    static SeContainer container;
    static Gson gson = new Gson();

    static String insertarPersona(Request req, Response res){
        var servicio = container.select(ServicioPersona.class).get();

        String body = req.body();
        Persona per = gson.fromJson(body, Persona.class);

        if(per==null) {
            // 404
            halt(404, "Tipo de dato incorrecto");
            return "No ingresado";
        }else{
            servicio.insert(per);
            return "Ingresado correctamente";
        }
    }

    static List<Persona> listarPersonas(Request req, Response res) {
        var servicio = container.select(ServicioPersona.class)
                .get();
        res.type("application/json");

        return servicio.findAll();
    }

    static Persona buscarPersona(Request req, Response res) {
        var servicio = container.select(ServicioPersona.class)
                .get();
        res.type("application/json");

        String _id = req.params(":id");

        var persona =  servicio.findById(Integer.valueOf(_id));

        if(persona==null) {
            // 404
            halt(404, "Persona no encontrada");
        }

        return persona;
    }

    static String eliminarPersona(Request req, Response res){
        var servicio = container.select(ServicioPersona.class).get();

        String id = req.params(":id");
        res.type("application/json");

        var persona =  servicio.findById(Integer.valueOf(id));

        if(persona==null) {
            // 404
            halt(404, "Persona no encontrada");
            return "Ha ocurrido un error";
        }else{
            servicio.delete(Integer.valueOf(id));
            return "Eliminado";
        }
    }

    static String actualizarPersona(Request req, Response res){
        var servicio = container.select(ServicioPersona.class).get();

        String body = req.body();
        Persona per = gson.fromJson(body, Persona.class);

        res.type("application/json");

        if(per==null) {
            // 404
            halt(404, "Ingrese a una persona");
            return "Ingrese un dato valido";
        }else{
            servicio.update(per);
            return "Actualizado correctamente";
        }
    }

    public static void main(String[] args) {
        container = SeContainerInitializer
                .newInstance()
                .initialize();

        port(8081);

        Gson gson = new Gson();
        get("/personas", Principal::listarPersonas, gson::toJson);
        get("/personas/:id", Principal::buscarPersona, gson::toJson);
        post("/personas/insertar", Principal::insertarPersona, gson::toJson);
        delete("/personas/eliminar/:id", Principal::eliminarPersona, gson::toJson);
        put("/personas/actualizar", Principal::actualizarPersona, gson::toJson);

        // Configuración CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.type("application/json");
        });
    }
}
