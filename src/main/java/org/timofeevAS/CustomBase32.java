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

     private static final int BITS_PER_CHAR = 5;
     private static final int BITS_PER_BYTE = 8;

    /**
     * Calculates the length of the input byte array excluding trailing padding characters ('=').
     *
     * @param input The byte array to analyze.
     * @return The length of the input byte array without trailing padding characters.
     */
    private static int lengthWithoutPadding(final byte[] input) {
        // Find index of first padding char ('=')
        int endIndex = input.length - 1;
        while (endIndex >= 0 && input[endIndex] == PADDING_CHAR) {
            endIndex--;
        }

        return endIndex + 1;
    }

    /**
     * Encodes a Base32-encoded byte array.
     *
     * @param input Byte array containing byte data.
     * @return Encoded Base32-byte array.
     */
    public static byte[] encode(final byte[] input) {
        // Initalize ByteArrayStream with maximum size for continue calculations.
        // (6 there is maximal number of padding char after encoded data)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(((input.length + 6) * BITS_PER_CHAR / BITS_PER_BYTE));

        int currentByte = 0,
                nextByte = 0,
                offset = 0,
                i = 0,
                index = 0;

        while (i < input.length) {
            currentByte = input[i] & 0xFF; // Convert (i)-byte to non-negative form [0;255]


            // If current offset going to span a byte boundary
            // If we offset greater than 3 we need to depend on previous byte
            if (offset > 3) {
                // Convert (i+1)-byte to non-negative form [0;255]
                // Else if we cant save nextByte implement as zero
                nextByte = (i + 1 < input.length) ? (input[i + 1] & 0xFF) : (0);

                index = currentByte & (0xFF >> offset); // Prepare bits from current byte, which isn't written
                offset = (offset + BITS_PER_CHAR) % BITS_PER_BYTE;

                index = index << offset; // Expand index to left with offset
                index = index | (nextByte >> (BITS_PER_BYTE - offset)); // Merge bits of two bytes

                // Can move to next byte, increase index (i)
                i++;
            } else {
                index = (currentByte >> (BITS_PER_BYTE - (offset + BITS_PER_CHAR))) & 0x1F; // Shift bits right and take first 5 bits
                offset = (offset + BITS_PER_CHAR) % BITS_PER_BYTE;

                if (offset == 0) {
                    // Can move to next byte, increase index (i)
                    i++;
                }
            }
            outputStream.write(ENCODE_TABLE[index]);
        }

        // Add padding chars
        while (offset % BITS_PER_BYTE != 0) {
            outputStream.write(PADDING_CHAR);
            offset += 5;
        }

        return outputStream.toByteArray();
    }

    /**
     * Decodes a Base32-encoded byte array.
     *
     * @param input Byte array containing Base32-encoded data.
     * @return Decoded byte array.
     */
    public static byte[] decode(final byte[] input){

        // Get length of input byte array without postfix padding char
        int tmpLength = lengthWithoutPadding(input);

        // Create output byte buffer with length, without padding chars ('=')
        byte[] outputBytes = new byte[(tmpLength * BITS_PER_CHAR / BITS_PER_BYTE)];


        int offset = 0;         // Offset for the current byte in the output array
        int letter = 0;         // Variable for storing a letter in the encoded byte array
        int indexOutput = 0;    // Index (pointer) of the byte (index) to be modified in the output array
        int decodedValue = 0;   // Value from DECODE_TABLE obtained by the letter

        // Iterate through each letter in input Base32 array and perform on this
        for (byte b : input) {
            letter = b & 0xFF; // Transform letter char into non-negative digit [0;255]

            // Skip characters outside the decode table
            if (letter >= DECODE_TABLE.length) {
                continue;
            }

            decodedValue = DECODE_TABLE[letter];

            // If decoded value from table is -1 we must ignore it
            if (decodedValue == -1) {
                continue;
            }

            // If we can't put all 5 bits into current byte
            if (offset > 3) {
                offset = (offset + 5) % 8;
                // Increase offset
                // Make intersection with current byte and make right shift with `offset`
                outputBytes[indexOutput] |= (byte) (decodedValue >> offset);

                // Move pointer in output array
                indexOutput++;

                // Condition to finish algorithm, when we go to end of output array
                if (indexOutput >= outputBytes.length) {
                    break;
                }
                // Save bits in next place in output array of old 5 bits
                outputBytes[indexOutput] |= (byte) (decodedValue << (8 - offset));
            }
            else {
                offset = (offset + BITS_PER_CHAR) % BITS_PER_BYTE;
                // Increase offset
                if (offset == 0) {
                    // If offset is zero can put all 5 bits on start of byte and continue
                    outputBytes[indexOutput] |= (byte) decodedValue;
                    indexOutput++;

                    // Condition to finish algorithm, when we go to end of output array
                    if (indexOutput >= outputBytes.length) {
                        break;
                    }
                }
                else {
                    // Merging with old bits making intersection with left shifting
                    outputBytes[indexOutput] |= (byte) (decodedValue << (BITS_PER_BYTE - offset));
                }
            }
        }
        return outputBytes;
    }

}

