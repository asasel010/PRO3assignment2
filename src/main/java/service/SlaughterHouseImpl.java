package service;

import database.*;
import io.grpc.stub.StreamObserver;
import generated.*;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class SlaughterHouseImpl extends SlaughterHouseGrpc.SlaughterHouseImplBase {

    private final ProductDAO productDAO = new ProductEntity();
    private final AnimalDAO animalDAO = new AnimalEntity();

    @Override
    public void getAnimalsByProduct(ProductRequest request, StreamObserver<AnimalListResponse> responseObserver) {
        System.out.println("Received getAnimalsByProduct request >>> " + request.getProductId());

        List<Animal> animals = new ArrayList<>();

        try {
            ArrayList<Integer> animalIds = productDAO.readAnimalsInProduct(request.getProductId());

            for (Integer id : animalIds) {
                Animal animal = fetchAnimalDetails(id);
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

    @Override
    public void getProductsByAnimal(AnimalRequest request, StreamObserver<ProductListResponse> responseObserver) {
        System.out.println("Received getProductsByAnimal request >>> " + request.getAnimalId());

        List<Product> products = new ArrayList<>();

        try {
            ArrayList<Integer> productIds = animalDAO.readProductsWithAnimal(request.getAnimalId());

            for (Integer id : productIds) {
                Product product = fetchProductDetails(id);
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

    private Animal fetchAnimalDetails(int animalId) throws SQLException {
        String sql = "SELECT * FROM slaughter_house.animal WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Animal.newBuilder()
                        .setAnimalId(rs.getInt("id"))
                        .setRegistrationNumber("") //do i delete this if its not in the database?
                        .setWeight(rs.getDouble("weight"))
                        .setType(rs.getString("type"))
                        .build();
            }
        }
        return Animal.newBuilder()
                .setAnimalId(animalId)
                .setRegistrationNumber("")
                .setWeight(0)
                .setType("unknown")
                .build();
    }

    private Product fetchProductDetails(int productId) throws SQLException {
        String sql = """
            SELECT p.id AS product_id,
                   CASE 
                       WHEN pkg.id IS NOT NULL THEN 'package'
                       WHEN ha.id IS NOT NULL THEN 'half_animal'
                       ELSE 'unknown'
                   END AS product_type
            FROM slaughter_house.product p
            LEFT JOIN slaughter_house.package pkg ON pkg.id = p.id
            LEFT JOIN slaughter_house.half_animal ha ON ha.id = p.id
            WHERE p.id = ?
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Product.newBuilder()
                        .setProductId(rs.getInt("product_id"))
                        .setProductType(rs.getString("product_type"))
                        .build();
            }
        }
        return Product.newBuilder()
                .setProductId(productId)
                .setProductType("unknown")
                .build();
    }
}

