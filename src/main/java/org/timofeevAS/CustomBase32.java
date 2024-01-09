package org.timofeevAS;

import java.io.ByteArrayOutputStream;

/**
 * CustomBase32 can encode and decode with Base32
 * <br>
 * <b>( source link: https://www.ietf.org/rfc/rfc3548.txt )</b>
 *
 * @author Alexandr Timofeev
 */

public class CustomBase32 {
    private static final byte[] ENCODE_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7',
    };

    private static final byte[] DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
            -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, // 30-3f 2-7
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 50-5a P-Z
            -1, -1, -1, -1, -1, // 5b-5f
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // 60-6f a-o
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 70-7a p-z
    };

    private static final int PADDING_CHAR = '=';

    /**
     * Encodes byte array to Base32 String.
     *
     * @param input Byte array to encode with Base32.
     * @return Byte array with encoded as Base32.
     */
    private static int lengthWithoutPadding(final byte[] input) {
        // Find index of first padding char ('=')
        int endIndex = input.length - 1;
        while (endIndex >= 0 && input[endIndex] == PADDING_CHAR) {
            endIndex--;
        }

        return endIndex + 1;
    }

    public static byte[] encode(final byte[] input) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(((input.length+6) * 8 / 5));

        int currentByte = 0,
                nextByte = 0,
                offset = 0,
                i = 0,
                index = 0;

        while (i < input.length) {
            currentByte = input[i] & 0xFF; // Convert (i)-byte to non-negative form [0;255]


            // If current offset going to span a byte boundary
            if (offset > 3) {
                // Convert (i+1)-byte to non-negative form [0;255]
                // Else if we cant save nextByte implement as zero
                nextByte = (i + 1 < input.length) ? (input[i + 1] & 0xFF) : (0);

                index = currentByte & (0xFF >> offset); // Prepare bits from current byte, which isn't written
                offset = (offset + 5) % 8;

                index = index << offset; // Expand index to left with offset
                index = index | (nextByte >> (8 - offset)); // Merge bits of two bytes

                // Can move to next byte, increase index (i)
                i++;
            } else {
                index = (currentByte >> (8 - (offset + 5))) & 0x1F; // Shift bits right and take first 5 bits
                offset = (offset + 5) % 8;

                if (offset == 0) {
                    // Can move to next byte, increase index (i)
                    i++;
                }
            }
            outputStream.write(ENCODE_TABLE[index]);

        }

        // Add padding chars
        while (offset % 8 != 0) {
            outputStream.write(PADDING_CHAR);
            offset += 5;
        }

        return outputStream.toByteArray();
    }

    public static byte[] decode(final byte[] input){

        int tmpLength = lengthWithoutPadding(input);
        // Create output byte buffer with length, without padding chars ('=')
        byte[] outputBytes = new byte[(tmpLength * 5 / 8)];


        int offset = 0, letter = 0, indexOutput = 0, decodedValue = 0;
        /*
        offset - for current byte in output array we have offset and storage them for calculate
        letter - variable for storage letter in encoded byte array
        indexOutput - index of byte which we change in output array
        decodedValue - value from DECODE_TABLE which we get by letter. Example: DECODED_TABLE[letter]
         */


        for (byte b : input) {
            letter = b & 0xFF;

            /* Skip chars outside the lookup table */
            if (letter >= DECODE_TABLE.length) {
                continue;
            }

            decodedValue = DECODE_TABLE[letter];

            /* If this digit is not in the table, ignore it */
            if (decodedValue == -1) {
                continue;
            }

            // If we can't put all 5 bits into current byte
            if (offset > 3) {
                offset = (offset + 5) % 8;
                // Increase offset
                outputBytes[indexOutput] |= (byte) (decodedValue >> offset);
                indexOutput++;

                if (indexOutput >= outputBytes.length) {
                    break;
                }
                outputBytes[indexOutput] |= (byte) (decodedValue << (8 - offset));
            }
            else {
                offset = (offset + 5) % 8;
                if (offset == 0) {
                    outputBytes[indexOutput] |= (byte) decodedValue;
                    indexOutput++;

                    if (indexOutput >= outputBytes.length) {
                        break;
                    }
                }
                else {
                    outputBytes[indexOutput] |= (byte) (decodedValue << (8 - offset));
                }
            }
        }
        return outputBytes;
    }

}

