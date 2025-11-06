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
                Animal animal = Animal.newBuilder()
                        .setAnimalId(id)
                        .setRegistrationNumber("")
                        .setWeight(0)
                        .setType("")
                        .build();
                animals.add(animal);
            }

            AnimalListResponse response = AnimalListResponse.newBuilder()
                    .addAllAnimals(animals)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
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
                Product product = Product.newBuilder()
                        .setProductId(id)
                        .setProductType("")
                        .build();
                products.add(product);
            }

            ProductListResponse response = ProductListResponse.newBuilder()
                    .addAllProducts(products)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}

