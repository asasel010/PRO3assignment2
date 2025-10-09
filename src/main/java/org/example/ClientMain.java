package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientMain {
    public static void main(String[] args)
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 3004)
                .usePlaintext()
                .build();

/*        TextConverterGrpc.TextConverterBlockingStub stub =
                TextConverterGrpc.newBlockingStub(channel);

        RequestText request = RequestText.newBuilder()
                .setInputText("rpc with streaming based on http/2")
                .build();

        ResponseText response = stub.toUpper(request);
        System.out.println("Response: " + response.getOutputText());*/

        channel.shutdown();
    }
}
