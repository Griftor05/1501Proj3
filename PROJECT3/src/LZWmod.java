import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;

/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;        // number of input chars
    public static final int startW = 9;
    private static int W = startW;         // base codeword width (will get longer as we get more keycodes)
    private static int maxW = 16;
    private static final int L = (int)Math.pow(2, maxW);       // number of codewords = 2^W
    private static boolean reset = false;
    private static boolean tablefull = false;

    // Compresses the file in StdIn to StdOut
    public static void compress() {
        // Steps of Compression:
        // - Step 1: Read in a word
        //        If it's in the dictionary, read in another character
        //        If it isn't, add it to the dictionary, and add the n - 1 len word to the file
        TST<Integer> st = new TST<Integer>();
        DLB mydlb = new DLB();
        StringBuilder readInWord = new StringBuilder();
        int counter;

        // Initialize our structures with all the ascii codewords
        for(counter = 0; counter < 256; counter++){
            String addedchar = Character.toString((char)counter);
            mydlb.add(addedchar);
            st.put(addedchar, counter);
        }
        mydlb.add("__NULL__");
        st.put("__NULL__", counter++);
        // NoChar == 256char

        while(true){
            try{
                // Read in a character
                char nextchar = BinaryStdIn.readChar();
                readInWord.append(nextchar);
                int inDLB = mydlb.searchPrefix(readInWord);
                // If it isn't a word and the table isn't full, we'll want to add it
                if((inDLB == 0 || inDLB == 1) && !tablefull){
                    String toAdd = readInWord.toString();
                    mydlb.add(toAdd);
                    st.put(toAdd, counter++);

                    // Now we should check if counter got too big
                    if(Math.log(counter) / Math.log(2) == W){
                        W++;
                    }
                    if(W > maxW){
                        W--;
                    }
                    if(counter + 2 == L){
                        tablefull = true;
                    }

                    // Write the n - 1 word to the output
                    String totranslate = readInWord.substring(0, readInWord.length() - 1);
                    Integer towrite = st.get(totranslate);
                    readInWord = new StringBuilder().append(nextchar);
                    BinaryStdOut.write(towrite, W);

                    // And then if the table is full, we wanna reset everything
                    if(tablefull && reset){
                        W = startW;
                        tablefull = false;

                        mydlb = new DLB();
                        st = new TST<Integer>();

                        // Also Initialize our structures with all the ascii codewords
                        for(counter = 0; counter < 256; counter++){
                            String addedchar = Character.toString((char)counter);
                            mydlb.add(addedchar);
                            st.put(addedchar, counter);
                        }
                        mydlb.add("__NULL__");
                        st.put("__NULL__", counter++);
                    }

                } // If the table is full, we want to just write the n - 1 word to the file
                else if((inDLB == 0 || inDLB == 1) && tablefull){
                    Integer towrite = st.get(readInWord.substring(0, readInWord.length() - 1));
                    readInWord = new StringBuilder(readInWord.substring(readInWord.length() - 1));
                    BinaryStdOut.write(towrite, W);
                } // Otherwise, keep looping
                else{
                    continue;
                }

            }
            catch(NoSuchElementException e){
                // If we've reached the end of the file, push out the last codeword
                // And I'm like 99% sure it'll be in st, so if not, we'll have to fix it.
                Integer towrite = st.get(readInWord.toString());
                BinaryStdOut.write(towrite, W);
                BinaryStdOut.flush();
                break;
                // We've reached the end of the file.
            }
        }
    }

    public static void expand() {
        // Steps of expanding:
        // 1 - Read in an integer
        //    If the integer is already declared, grab it and write that value to the output file
        //    If the integer is equal to the counter, it is the word + the first letter of the word
        // 2 - Repeat
        reset = BinaryStdIn.readBoolean();
        String[] st = new String[L];
        int counter;
        for(counter = 0; counter < 256; counter++){
            st[counter] = Character.toString((char)counter);
        }
        // NoChar == 256
        st[counter++] = "__NULL__";
        int readIn;
        String toWrite;
        int firstNum = BinaryStdIn.readInt(W);
        String toRemember = st[firstNum];
        BinaryStdOut.write(toRemember);

        while(true){
            try{
                if(!tablefull) {
                    readIn = BinaryStdIn.readInt(W);

                    // Need to get the corresponding word from the array
                    if (readIn == counter) {
                        // Then the word is equal to the word plus the first letter of itself
                        toWrite = toRemember;
                        toWrite = toWrite + toWrite.substring(0, 1);
                    } else if (readIn < counter) {
                        // Otherwise, the word is just gotten from the array
                        toWrite = st[readIn];
                    } else {
                        BinaryStdOut.flush();
                        throw new RuntimeException("Something is decoding funky.");
                    }
                    // In either case, first add the old string to the array
                    if (!tablefull) {
                        st[counter++] = toRemember + toWrite.substring(0, 1);
                    }

                    // Then write out the new string
                    BinaryStdOut.write(toWrite);
                    // Then do the reset
                    toRemember = toWrite;

                    // Also check if counter has gotten too big
                    if (Math.log(counter + 2) / Math.log(2) == W) {
                        W++;
                    }
                    if (W > maxW) {
                        W--;
                    }
                    if (counter + 3 == L) {
                        tablefull = true;
                        // We'll need to stick in our last value if we're done here
                        if(!reset){
                            readIn = BinaryStdIn.readInt(W);

                            // Need to get the corresponding word from the array
                            if (readIn == counter) {
                                // Then the word is equal to the word plus the first letter of itself
                                toWrite = toRemember;
                                toWrite = toWrite + toWrite.substring(0, 1);
                            } else if (readIn < counter) {
                                // Otherwise, the word is just gotten from the array
                                toWrite = st[readIn];
                            }
                            // Plug in that last value, write it out, and we should be good, I think.
                            st[counter++] = toRemember + toWrite.substring(0, 1);

                            BinaryStdOut.write(toWrite);
                            toRemember = toWrite;
                        }
                    }
                    if (tablefull && reset) {
                        // Let's see what happens if we don't read in an extra word
                        W = startW;
                        tablefull = false;
                        st = new String[L];

                        for (counter = 0; counter < 256; counter++) {
                            st[counter] = Character.toString((char) counter);
                        }
                        // NoChar == 256
                        st[counter++] = "__NULL__";

                        int nextin = BinaryStdIn.readInt(W);
                        toRemember = st[nextin];
                        BinaryStdOut.write(toRemember);
                    }
                }
                else{
                    // No need to do all that extra junk
                    readIn = BinaryStdIn.readInt(W);

                    toWrite = st[readIn];
                    BinaryStdOut.write(toWrite);
                }
            }
            catch(NoSuchElementException e){
                // Just have to write out this last bit, and then done
                BinaryStdOut.flush();
                break;
                // Got to the end of the file
            }
        }
    }


    public static void main(String[] args) {
        // Write a 1 if you wanna compress and dump the table when it gets full
        //    write a 0 otherwise
        if (args[0].equals("-")) {
            if (args.length != 3) {
                if (args[1].equals("r")) reset = true;
                else if (args[1].equals("n")) reset = false;
                else throw new RuntimeException("Illegal command line argument");
                // To be used in the decompression
                BinaryStdOut.writeBit(reset);
                compress();
            } else {
                throw new RuntimeException("Incorrect number of parameters");
            }
        } else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }
}
