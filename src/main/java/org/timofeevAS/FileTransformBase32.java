package org.timofeevAS;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


import static java.nio.file.StandardOpenOption.*;

public class FileTransformBase32 {
    // For configure buffer size should take 40*K BUFEER_SIZE to avoid problem with padding
    private static final int BUFFER_SIZE = 40*(26215); // About 1 MB
    //private static final int BUFFER_SIZE = 40; // About 1 MB
    public static void encodeFileToBase32(File inputFile) throws IOException, IllegalArgumentException {
        // Checking if the file doesn't exist
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a regular file.");
        }

        // Creating file for output
        File outputFile = new File(inputFile.getParent(), inputFile.getName() + ".b32");

        try (FileChannel inputChannel = FileChannel.open(inputFile.toPath(), READ);
             FileChannel outputChannel = FileChannel.open(outputFile.toPath(),CREATE,WRITE,TRUNCATE_EXISTING);
        ) {

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();

                byte[] part = new byte[buffer.limit()];
                buffer.get(part);

                ByteBuffer encodedBuffer = ByteBuffer.wrap(CustomBase32.encode(part));
                outputChannel.write(encodedBuffer);

                buffer.clear();
            }
        }
    }

    public static void decodeFileFromBase32(File inputFile) throws IOException, IllegalArgumentException{
        // Checking if the file doesn't exist
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a regular file.");
        }

        // Get filename without extension .b32
        String baseName = inputFile.getName().replaceFirst("\\.b32$", "");

        // Creating file with name: decoded_FILENAME.EXTENSION
        File outputFile = new File(inputFile.getParent(), "decoded_" + baseName);

        try (FileChannel inputChannel = FileChannel.open(inputFile.toPath(), READ);
             FileChannel outputChannel = FileChannel.open(outputFile.toPath(),CREATE,WRITE,TRUNCATE_EXISTING);
        ) {

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();

                byte[] part = new byte[buffer.limit()];
                buffer.get(part);

                ByteBuffer decodedBuffer = ByteBuffer.wrap(CustomBase32.decode(part));
                outputChannel.write(decodedBuffer);

                buffer.clear();
            }
        }
    }
}
