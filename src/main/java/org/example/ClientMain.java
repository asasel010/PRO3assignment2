package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import via.pro3.grpc.generated.*;

public class ClientMain {
    public static void main(String[] args)
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3004).usePlaintext().build();

        SlaughterHouseGrpc.SlaughterHouseBlockingStub stub = SlaughterHouseGrpc.newBlockingStub(channel);

           AnimalRequest animalRequest = AnimalRequest.newBuilder().setAnimalId(1).build();

      ProductListResponse productResponse = stub.getProductsByAnimal(animalRequest);
      System.out.println("\nProducts for animal ID 2:");
      productResponse.getProductsList().forEach(product ->
          System.out.println(" - " + product.getProductType() + " (ID: " + product.getProductId() + "), trays: " + product.getTrayIdsList())
      );

      ProductRequest productRequest = ProductRequest.newBuilder().setProductId(1).build();

      AnimalListResponse animalResponse = stub.getAnimalsByProduct(productRequest);
      System.out.println("\nAnimals for product ID 1:");
      animalResponse.getAnimalsList().forEach(animal ->
          System.out.println(" - " + animal.getType() + " (" + animal.getRegistrationNumber() + "), weight: " + animal.getWeight())
      );

        channel.shutdown();
    }
}

