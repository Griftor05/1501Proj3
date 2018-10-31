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
    private static final int L = 4096;       // number of codewords = 2^W
    public static final int startW = 12;
    private static int W = 12;         // base codeword width (will get longer as we get more keycodes)

    private static void  pushOut(int toWrite, int counter){
        // First, need to check if we need to bump up codeword size
        // Have to divide by ln 2 because of logarithm rules (I want log base 2 of counter)
        if(Math.log(counter) / Math.log(2) >= W){
            W ++;
        }

        // Then we need to write toWrite as a W-width number to the file
        BinaryStdOut.write(toWrite, W);
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

        StringBuilder s = new StringBuilder();
        StringBuilder writebuffer = new StringBuilder();
        char mybyte;
        int lookupReturn;

        // Read in the file char by char, find the longest string in the dlb
        while (true){
            try{
                mybyte = BinaryStdIn.readChar();
                s.append(mybyte);
                lookupReturn = mydlb.searchPrefix(s);

                // The only two end conditions are trying to plug in a nonexistent word (Which could be a prefix)
                // If we get a 2 or a 3, we'll just have to keep looping
                    if(lookupReturn == 0 || lookupReturn == 1){
                        // We have a new word, so let's add it to the TST and the DLB
                        String toadd = s.toString();
                        st.put(toadd, counter++);
                        mydlb.add(toadd);
                    // We know that s with 1 less character is always valid (could be proven inductively, ha ha)
                    //     so push that mapped int to the printbuffer and update s with just the byte
                    int gotback = st.get(s.substring(0, s.length() - 1));
                    pushOut(gotback, counter);
                    s.delete(0, s.length() - 1);
                }
                // Else, keep looping
                // Need this code, but definitely not here
            }
            catch(NoSuchElementException e){
                // Got to the end of the file
                break;
            }
        }

        // Apparently R is the codeword for EOF
        // Make sure that we've got everything in the file
        BinaryStdOut.flush();
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() {
        int i; // next available codeword value
        String[] st = new String[L];
        W = startW;

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            codeword = BinaryStdIn.readInt(W);
            BinaryStdOut.write(val);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
