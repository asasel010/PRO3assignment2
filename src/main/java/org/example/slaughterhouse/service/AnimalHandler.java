package org.example.slaughterhouse.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.slaughterhouse.database.DatabaseConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;


public class AnimalHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (method.equalsIgnoreCase("POST") && path.equals("/animals")) {
                handlePost(exchange);
            } else if (method.equalsIgnoreCase("GET")) {
                if (path.matches("/animals/\\d+")) {
                    int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                    handleGetById(exchange, id);
                } else if (path.equals("/animals")) {
                    handleQuery(exchange, query);
                } else {
                    sendResponse(exchange, 404, "Not found");
                }
            } else {
                sendResponse(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws Exception {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Map<String, String> data = parseJson(body);

        int id = Integer.parseInt(data.getOrDefault("id", "0"));
        String regNum = data.get("registrationNumber");
        String origin = data.get("origin");
        String dateStr = data.get("arrivedDate");
        Integer weight = data.containsKey("weight") ? Integer.parseInt(data.get("weight")) : null;
        String type = data.get("type");

        LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement update = conn.prepareStatement("""
                    UPDATE slaughter_house.animal
                    SET registration_number=?, origin=?, arrived_date=?, weight=?, type=?
                    WHERE id=?;
                """);
            update.setString(1, regNum);
            update.setString(2, origin);
            if (date != null)
                update.setDate(3, Date.valueOf(date));
            else
                update.setNull(3, Types.DATE);
            if (weight != null)
                update.setInt(4, weight);
            else
                update.setNull(4, Types.INTEGER);
            update.setString(5, type);
            update.setInt(6, id);
            int updated = update.executeUpdate();

            if (updated == 0) {
                PreparedStatement insert = conn.prepareStatement("""
                        INSERT INTO slaughter_house.animal (id, weight, type, registration_number, origin, arrived_date)
                        VALUES (?, ?, ?, ?, ?, ?);
                    """);
                insert.setInt(1, id);
                insert.setObject(2, weight);
                insert.setString(3, type);
                insert.setString(4, regNum);
                insert.setString(5, origin);
                if (date != null)
                    insert.setDate(6, Date.valueOf(date));
                else
                    insert.setNull(6, Types.DATE);
                insert.executeUpdate();
            }
        }

        sendResponse(exchange, 200, "Animal registered/updated successfully");
    }

    private void handleGetById(HttpExchange exchange, int id) throws Exception {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("""
                    SELECT * FROM slaughter_house.animal WHERE id = ?;
                """);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> animal = new LinkedHashMap<>();
                animal.put("id", rs.getInt("id"));
                animal.put("weight", rs.getInt("weight"));
                animal.put("type", rs.getString("type"));
                animal.put("registrationNumber", rs.getString("registration_number"));
                animal.put("origin", rs.getString("origin"));
                animal.put("arrivedDate", rs.getDate("arrived_date"));
                sendResponse(exchange, 200, toJson(animal));
            } else {
                sendResponse(exchange, 404, "Animal not found");
            }
        }
    }

    private void handleQuery(HttpExchange exchange, String query) throws Exception {
        if (query == null) {
            sendResponse(exchange, 400, "Specify ?date=YYYY-MM-DD or ?origin=Farm");
            return;
        }

        String sql;
        String param;
        boolean byDate = false;
        if (query.startsWith("date=")) {
            sql = "SELECT * FROM slaughter_house.animal WHERE arrived_date = ?";
            param = query.substring(5);
            byDate = true;
        } else if (query.startsWith("origin=")) {
            sql = "SELECT * FROM slaughter_house.animal WHERE LOWER(origin) = LOWER(?)";
            param = query.substring(7);
        } else {
            sendResponse(exchange, 400, "Invalid query param");
            return;
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (byDate)
                stmt.setDate(1, Date.valueOf(LocalDate.parse(param, DateTimeFormatter.ISO_DATE)));
            else
                stmt.setString(1, param);

            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> animals = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> a = new LinkedHashMap<>();
                a.put("id", rs.getInt("id"));
                a.put("weight", rs.getInt("weight"));
                a.put("type", rs.getString("type"));
                a.put("registrationNumber", rs.getString("registration_number"));
                a.put("origin", rs.getString("origin"));
                a.put("arrivedDate", rs.getDate("arrived_date"));
                animals.add(a);
            }
            sendResponse(exchange, 200, toJson(animals));
        }
    }

    private String toJson(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            int i = 0;
            for (var e : map.entrySet()) {
                if (i++ > 0) sb.append(",");
                sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            }
            sb.append("}");
            return sb.toString();
        } else if (obj instanceof List<?> list) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }
        return "{}";
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replaceAll("[{}\"]", "");
        for (String pair : json.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    private void sendResponse(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
