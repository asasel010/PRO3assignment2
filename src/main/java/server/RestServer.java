package server;

import com.sun.net.httpserver.*;
import org.example.slaughterhouse.service.AnimalHandler;

import java.net.InetSocketAddress;
import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.sql.*;

public class RestServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/animals", new AnimalHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("HTTP REST server started on port " + port);
    }
}