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
    private static final int W = 12;         // base codeword width (will get longer as we get more keycodes)

    public static void compress() {
        TST<Integer> st = new TST<Integer>();
        char mybyte;
        DLB mydlb = new DLB();
        // Have to pre-populate with the codewords
        for(char c = 0; c < 255; c++){
            String mychar = Character.toString(c);
            mydlb.add(mychar);
            st.put(mychar, (int)c);
        }

        StringBuilder s = new StringBuilder();
        int lookupReturn;

        while (true){
            try{
                mybyte = BinaryStdIn.readChar();
                s.append(mybyte);
                lookupReturn = mydlb.searchPrefix(s);
                if(lookupReturn == 0){

                }




                BinaryStdOut.write(st.get(s.toString()), W);      // Print s's encoding.
            }
            catch(NoSuchElementException e){
                // Got to the end of the file
                break;
            }
        }

        // Apparently R is the codeword for EOF
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() {
        int i; // next available codeword value
        String[] st = new String[L];

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
