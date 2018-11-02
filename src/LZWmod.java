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
    private static final int L = 65536;       // number of codewords = 2^W
    public static final int startW = 9;
    private static int W = startW;         // base codeword width (will get longer as we get more keycodes)
    private static int maxW = 16;
    private static boolean reset = false;
    private static boolean tablefull = false;
    //private static FileWriter codewordFileWriter;

    // The boolean return tells you if you have to reset the dictionary or not
    private static boolean  pushOut(int toWrite, int counter){
        // First, need to check if we need to bump up codeword size
        // Have to divide by ln 2 because of logarithm rules (I want log base 2 of counter)
        if(Math.log(counter) / Math.log(2) == W){
            W ++;
        }

        //writeToTheDebugFile("Wrote " + nlen(W, toWrite) + " or hex " + Integer.toHexString(toWrite) + " or table entry: " + toWrite + "\n");

        if(toWrite > counter){
            throw new RuntimeException("Houston, we have an encoding issue.");
        }

        // Then we need to write toWrite as a W-width number to the file
        BinaryStdOut.write(toWrite, W);

        return (reset && Math.log(counter + 1) / Math.log(2) >= maxW);
    }

    public static void compress() {
        TST<Integer> st = new TST<Integer>();
        DLB mydlb = new DLB();
        int counter = 0;

        // Have to pre-populate with the codewords
        for(counter = 0; counter <= 255; counter++){
            String mychar = Character.toString((char)counter);
            mydlb.add(mychar);
            st.put(mychar, counter);
        }

        st.put("_EOF_", counter++); // Add in "" as your EOF character

        StringBuilder s = new StringBuilder();
        StringBuilder writebuffer = new StringBuilder();
        char mybyte;
        int lookupReturn;
        boolean needToReset;

        // Read in the file char by char, find the longest string in the dlb
        while (true){
            try{
                mybyte = BinaryStdIn.readChar();
                s.append(mybyte);
                lookupReturn = mydlb.searchPrefix(s);

                // The only two end conditions are trying to plug in a nonexistent word (Which could be a prefix)
                // If we get a 2 or a 3, we'll just have to keep looping
                if(lookupReturn == 0 || lookupReturn == 1) {
                    // We have a new word, so let's add it to the TST and the DLB, but only if the table isn't full
                    if(W < maxW || reset || (Math.log(counter) / Math.log(2) <= maxW)) {
                        String toadd = s.toString();
                        st.put(toadd, counter);
                        mydlb.add(toadd);
                    }

                    // We know that s with 1 less character is always valid (could be proven inductively, ha ha)
                    //     so push that mapped int to the printbuffer and update s with just the byte
                    int gotback = st.get(s.substring(0, s.length() - 1));
                    needToReset = pushOut(gotback, counter++);
                    if(needToReset){
                        //Need to repopulate the table
                        mydlb = new DLB();
                        st = new TST<Integer>();
                        W = startW;
                        for(counter = 0; counter <= 255; counter++){
                            String mychar = Character.toString((char)counter);
                            mydlb.add(mychar);
                            st.put(mychar, counter);
                        }
                    }
                    s.delete(0, s.length() - 1);
                }
            }
            catch(NoSuchElementException e) {
                    // Got to the end of the file
                break;
            }
        }
                // Else, keep looping
                // Need this code, but definitely not here

        // Apparently R is the codeword for EOF
        // Make sure that we've got everything in the file
        BinaryStdOut.flush();
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    private static String nlen(int w, int codeword){
        StringBuilder returner = new StringBuilder(Integer.toBinaryString(codeword)).reverse();
        while(returner.length() < w)
            returner.append("0");
        returner.reverse();
        return returner.toString();
    }


    public static void expand() {
        // First, need to determine if we're in reset mode or not
        // If true, have to include reset code
        reset = BinaryStdIn.readBoolean();

        String[] st = new String[L];

        // initialize symbol table with all 1-character strings
        int counter;
        // Have to pre-populate with the codewords
        for(counter = 0; counter <= 255; counter++){
            String mychar = Character.toString((char)counter);
            st[counter] = mychar;
        }

        // Put in 256 as the EOF
        st[counter++] = Character.toString((char)R);
        // Read in your first word, and you're off to the races
        int codeword = BinaryStdIn.readInt(W);
        StringBuilder oldWord = new StringBuilder(st[codeword]);
        BinaryStdOut.write(oldWord.toString());
        //writeToTheDebugFile("Read " + nlen(W, codeword) + " or hex " + Integer.toHexString(codeword) + " or table entry: " + codeword + "\n");

        while (true) {
            // need to check if codeword size has to be biggified
            if(W < maxW && Math.log(counter + 1) / Math.log(2) == W){
                W ++;
            }
            else{
                // Check if the table is full
                if(Math.log(counter + 1) / Math.log(2) == W) {
                    tablefull = true;
                }
            }

            // Read in the next codeword
            codeword = BinaryStdIn.readInt(W);
            //System.out.println("The " + counter + "th number binary is: " + nlen(W, codeword));
            //writeToTheDebugFile("Read " + nlen(W, codeword) + " or hex " + Integer.toHexString(codeword) + " or table entry: " + codeword + "\n");

            StringBuilder newWord;
            if(codeword == counter){ // This takes care of the case when a value is used immediately after being encoded
                // Should never get a codeword larger than counter
                oldWord = oldWord.append(oldWord.substring(0, 1));
                newWord = new StringBuilder(oldWord);
            }
            else if(codeword < counter){ // Otherwise, the old word appends the first letter of the new string
                newWord = new StringBuilder(st[codeword]);
                oldWord.append(newWord.substring(0, 1));
            }
            else{
                throw new RuntimeException("Something went wrong with the decoding!");
            }

            if (codeword == R) break;

            // If we wanna reset the table and it's full, reset it.
            if(reset && tablefull){
                tablefull = false;
                W = startW;
                st = new String[L];

                // Prepopulate with the chars
                for(counter = 0; counter <= 255; counter++){
                    String mychar = Character.toString((char)counter);
                    st[counter] = mychar;
                }

                // Put in 256 as the EOF
                st[counter++] = Character.toString((char)R);
            }

            // HERE IS WHERE WE ACTUALLY DO THE OUTPUTTING. FIGURE THAT OUT, BUDDY.
            // BinaryStdOut.write(newWord.toString());

        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {

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
