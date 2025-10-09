package service;

import io.grpc.stub.StreamObserver;
import via.pro3.grpc.generated.*;

public class SlaughterHouseImpl extends SlaughterHouseGrpc.SlaughterHouseImplBase
{
    public void getAnimalsByProduct(ProductRequest request, StreamObserver<AnimalListResponse> responseObserver) {

    }

    public void getProductsByAnimal(AnimalRequest request, StreamObserver<ProductListResponse> responseObserver) {

    }
}
