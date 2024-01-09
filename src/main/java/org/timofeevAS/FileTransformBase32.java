package org.timofeevAS;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


import static java.nio.file.StandardOpenOption.*;

public class FileTransformBase32 {
    // For configure buffer size should take 40*N buffer size: to avoid problem with padding
    // Cause when buffer size is divided without remainder -> doesn't get padding chars.
    // Last piece of any file will correctly end with padding chard if it needs.
    private static final int BUFFER_SIZE = 40*(26215); // This digit about 1 MB.

    /**
     * Encodes a file to Base32 format.
     *
     * @param inputFile The file to be encoded.
     * @throws IOException              If an I/O error occurs.
     * @throws IllegalArgumentException If the input file is not a regular file or does not exist.
     */
    public static void encodeFileToBase32(File inputFile) throws IOException, IllegalArgumentException {
        // Checking if the file doesn't exist
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a regular file.");
        }

        // Creating file for output with pseudo-extension *.b32, which marks new file for user
        File outputFile = new File(inputFile.getParent(), inputFile.getName() + ".b32");

        // Block with manipulating on files
        try (FileChannel inputChannel = FileChannel.open(inputFile.toPath(), READ);
             FileChannel outputChannel = FileChannel.open(outputFile.toPath(),CREATE,WRITE,TRUNCATE_EXISTING);
        ) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            // Manipulate on file with chunks of data
            while (inputChannel.read(buffer) != -1) {
                // Reset buffer cursor to 0 and set ready buffer to read
                buffer.flip();


                byte[] part = new byte[buffer.limit()];
                buffer.get(part);

                // Encode part from file and write them in output file
                ByteBuffer encodedBuffer = ByteBuffer.wrap(CustomBase32.encode(part));
                outputChannel.write(encodedBuffer);

                // Clear buffer and get ready to use again
                buffer.clear();
            }
        }
    }

    /**
     * Decodes a file from Base32 format.
     *
     * @param inputFile The file to be encoded.
     * @throws IOException              If an I/O error occurs.
     * @throws IllegalArgumentException If the input file is not a regular file or does not exist.
     */
    public static void decodeFileFromBase32(File inputFile) throws IOException, IllegalArgumentException{
        // Checking if the file doesn't exist
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("File does not exist or is not a regular file.");
        }

        // Get filename without extension .b32
        String baseName = inputFile.getName().replaceFirst("\\.b32$", "");

        // Creating file with name: decoded_FILENAME.OLD_EXTENSION
        File outputFile = new File(inputFile.getParent(), "decoded_" + baseName);

        try (FileChannel inputChannel = FileChannel.open(inputFile.toPath(), READ);
             FileChannel outputChannel = FileChannel.open(outputFile.toPath(),CREATE,WRITE,TRUNCATE_EXISTING);
        ) {

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            // Manipulate on file with chunks of data
            while (inputChannel.read(buffer) != -1) {
                // Reset buffer cursor to 0 and set ready buffer to read
                buffer.flip();

                byte[] part = new byte[buffer.limit()];
                buffer.get(part);

                // Decode part from file and write them in output file
                ByteBuffer encodedBuffer = ByteBuffer.wrap(CustomBase32.decode(part));
                outputChannel.write(encodedBuffer);

                // Clear buffer and get ready to use again
                buffer.clear();
            }
        }
    }
}
