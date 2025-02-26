package org.example;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) {
        try {
            GraphQLServer.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
