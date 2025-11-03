package org.example.slaughterhouse.service;

import io.grpc.stub.StreamObserver;
import org.example.slaughterhouse.database.DatabaseConnection;
import org.example.slaughterhouse.generated.*;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class SlaughterHouseImpl extends SlaughterHouseGrpc.SlaughterHouseImplBase
{
    public void getAnimalsByProduct(ProductRequest request, StreamObserver<AnimalListResponse> responseObserver) {
        System.out.println("Received request >>> " + request.toString());

        List<Animal> animals = new ArrayList<>();
        String sql = """
            SELECT DISTINCT a.animal_id, a.registration_number, a.weight, a.type
            FROM animals a
            JOIN parts p ON a.animal_id = p.animal_id
            JOIN tray_parts tp ON p.part_id = tp.part_id
            JOIN product_trays pt ON tp.tray_id = pt.tray_id
            WHERE pt.product_id = ?
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, request.getProductId());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Animal animal = Animal.newBuilder()
                        .setAnimalId(result.getInt("animal_id"))
                        .setRegistrationNumber(result.getString("registration_number"))
                        .setWeight(result.getDouble("weight"))
                        .setType(result.getString("type"))
                        .build();
                animals.add(animal);
            }

            AnimalListResponse response = AnimalListResponse.newBuilder()
                    .addAllAnimals(animals)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }

    public void getProductsByAnimal(AnimalRequest request, StreamObserver<ProductListResponse> responseObserver) {
        System.out.println("Received request >>> " + request.toString());

        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT DISTINCT pr.product_id, pr.product_type, pt.tray_id
            FROM products pr
            JOIN product_trays pt ON pr.product_id = pt.product_id
            JOIN tray_parts tp ON pt.tray_id = tp.tray_id
            JOIN parts pa ON tp.part_id = pa.part_id
            WHERE pa.animal_id = ?
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, request.getAnimalId());
            ResultSet result = statement.executeQuery();
            
            while (result.next()) {
                int productId = result.getInt("product_id");
                String productType = result.getString("product_type");
                int trayId = result.getInt("tray_id");

                Product product = Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .addTrayIds(trayId)
                        .build();

                products.add(product);
            }

            ProductListResponse response = ProductListResponse.newBuilder()
                    .addAllProducts(products)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (SQLException e) {
            responseObserver.onError(e);
        }
    }
}
