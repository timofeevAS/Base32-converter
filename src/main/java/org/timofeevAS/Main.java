package org.timofeevAS;


import java.io.File;
import java.io.IOException;
import java.sql.Time;

public class Main {
    public static void main(String[] args) {

        String textToEncode = "BENCHPRESS MODE ON";
        System.out.println("Input: "+textToEncode);
        byte[] inputBytes = textToEncode.getBytes();

        byte[] encodedBase32 = CustomBase32.encode(inputBytes);
        byte[] decodedBase32 = CustomBase32.decode(encodedBase32);

        System.out.println("Encoded Base32 as String: " + new String(encodedBase32));
        System.out.println("Decoded Base32 as String: " + new String(decodedBase32));

        File inputFile = new File("src/main/resources/openedu.png");

        try {
            long startTime = System.currentTimeMillis();
            FileTransformBase32.encodeFileToBase32(inputFile);
            System.out.println((System.currentTimeMillis()-startTime));
            System.out.println("File encoded successfully.");
            System.out.println("Decode...");
            startTime = System.currentTimeMillis();
            FileTransformBase32.decodeFileFromBase32(new File("src/main/resources/openedu.png.b32"));
            System.out.println((System.currentTimeMillis()-startTime));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
