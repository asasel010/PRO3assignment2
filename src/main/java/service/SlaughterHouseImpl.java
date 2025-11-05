package service;

import io.grpc.stub.StreamObserver;
import database.DatabaseConnection;
import generated.*;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class SlaughterHouseImpl extends SlaughterHouseGrpc.SlaughterHouseImplBase
{
    public void getAnimalsByProduct(ProductRequest request, StreamObserver<AnimalListResponse> responseObserver) {
        System.out.println("Received request >>> " + request.toString());

        List<Animal> animals = new ArrayList<>();
        String sql = """
            SELECT DISTINCT a.id AS animal_id, a.weight, a.type
            FROM slaughter_house.animal a
            JOIN slaughter_house.animal_part ap ON a.id = ap.animal_id
            JOIN slaughter_house.product_part pp ON ap.id = pp.animal_part_id
            WHERE pp.product_id = ?
        """;


        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, request.getProductId());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Animal animal = Animal.newBuilder()
                        .setAnimalId(result.getInt("animal_id"))
                        .setRegistrationNumber("")
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
            SELECT DISTINCT p.id AS product_id
            FROM slaughter_house.product p
            JOIN slaughter_house.product_part pp ON p.id = pp.product_id
            JOIN slaughter_house.animal_part ap ON ap.id = pp.animal_part_id
            WHERE ap.animal_id = ?
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
                        .setProductId(result.getInt("product_id"))
                        .setProductType("")
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
