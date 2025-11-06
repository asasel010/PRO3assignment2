import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import server.RestServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class RestServerTest {
    private static final String BASE_URL = "http://localhost:8080/animals";

    @BeforeAll
    static void setup() throws Exception {
        new Thread(() -> {
            try {
                RestServer.main(new String[]{});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        Thread.sleep(1000);
    }

    @Test
    void testRegisterAndGetAnimal() throws Exception {
        JSONObject animal = new JSONObject()
                .put("id", 9999)
                .put("registrationNumber", "REG-9999")
                .put("origin", "Farm")
                .put("arrivedDate", "2025-11-06")
                .put("weight", 450)
                .put("type", "cow");

        sendPost(animal.toString());

        JSONObject result = sendGetObject(BASE_URL + "/9999");
        assertEquals("Farm", result.getString("origin"));
        assertEquals("cow", result.getString("type"));
    }

    @Test
    void testQueryByOrigin() throws Exception {
        JSONArray results = sendGetArray(BASE_URL + "?origin=Farm");
        assertTrue(results.length() > 0, "No animals found for origin 'Farm'");
    }

    private void sendPost(String json) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        assertEquals(200, conn.getResponseCode());
    }

    private JSONObject sendGetObject(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return new JSONObject(br.readLine());
        }
    }

    private JSONArray sendGetArray(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return new JSONArray(br.readLine());
        }
    }
}
