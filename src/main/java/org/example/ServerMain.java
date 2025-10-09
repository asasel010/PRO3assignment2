package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.SlaughterHouseImpl;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args)
    {
        try{
            Server server = ServerBuilder.forPort(3004)
                    .addService(new SlaughterHouseImpl())
                    .build();
            server.start();
            System.out.println("Server started, listening on " + server.getPort());
            server.awaitTermination();
        }catch(IOException | InterruptedException ioe){
            System.out.println("Caught exception: " + ioe);
        }
    }
}
