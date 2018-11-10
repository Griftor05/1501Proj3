import java.util.*;

// A class that's a Linked List implementation of a DLB Trie running underneath a Dictionary
// With this, we should have greatly increased time over the MyDictionary solution
public class DLB{
    // The Trie will be constructed from a series of connected nodes
    // Each of which will hold a letter and a linked list of children
    // A linked list is used to save on space
    private class Node {
        private char letter;
        private int size;
        private LinkedList<Node> nodelist = new LinkedList<Node>();
        // Not as memory intensive because it only holds chars
        // Use this to avoid having to run through the Linked List lots of times
        private ArrayList<Character> alphabet = new ArrayList<Character>();

        public void setLetter(char c){
            letter = c;
        }

        public LinkedList<Node> getNodelist(){ return nodelist; }

        // toadd is a node to add to the internal linked list
        // should only be used if Node with char toadd.letter DOES NOT exist within the nodelist already
        public void addNode(Node toadd){
            nodelist.add(toadd);
            char toaddchar = toadd.getletter();
            alphabet.add(toaddchar);
            size += 1;
        }

        // Returns the length of the linked list
        public int getsize(){
            return size;
        }

        // Returns the letter of the current node
        public char getletter() {return letter;}

        // Returns whether the node's linked list has a node with char letter in it
        public boolean hasletter(char letter) {
            return alphabet.contains(letter);
        }

        // Returns the node with char letter in it
        // If it doesn't exist (it should, but hey), then it returns null
        public Node getnodewithchar(char letter){
            Iterator m = nodelist.listIterator();
            while(m.hasNext()){
                Node mynode = (Node) m.next();
                if(mynode.getletter() == letter){
                    return mynode;
                }
            }

            return null;
        }
    }

    // Create the root of the tree
    Node root = new Node();

    // The Interface requires 3 methods
    // Add, SearchPrefix, and a SearchPrefix w/ two arguments
    // Implementation to follow

    // Adds a string to our Trie
    public boolean add(String s){
        int i = 0;
        Node livenode = root;
        char chartoadd;

        while(i < s.length()){
            chartoadd = s.charAt(i);
            if(livenode.hasletter(chartoadd)){
                livenode = livenode.getnodewithchar(chartoadd);
            }
            else{
                Node newnode = new Node();
                newnode.setLetter(chartoadd);
                livenode.addNode(newnode);
                livenode = livenode.getnodewithchar(chartoadd);
            }
            i += 1;
        }

        Node endword = new Node();
        endword.setLetter('^');
        livenode.addNode(endword);

        return true;
    }

    // My implementation of searchPrefix
    // Takes in a Stringbuilder and sees if it's a member of the Trie
    // 0 - Not in Trie at all
    // 1 - Valid prefix, but not a word
    // 2 - Valid word, but not a prefix
    // 3 - Both a word and a prefix
    public int searchPrefix(StringBuilder s) {
        boolean isprefix = false;
        boolean isword = false;

        int i = 0;
        Node livenode = root;
        while(i < s.length()){
            char mychar = s.charAt(i);
            if(livenode.hasletter(mychar)){
                livenode = livenode.getnodewithchar(mychar);
            }
            else{
                return 0;
            }
            i += 1;
        }

        // Okay, so now we should have a final node with the last letter in it
        // If it has a child with the word terminator
        if(livenode.hasletter('^')) isword = true;
        // If it has more than one child (or just one and it isn't the word terminator)
        if(livenode.getsize() > 1 || (livenode.getsize() == 1 && !livenode.hasletter('^'))) isprefix = true;


        if(isprefix && isword) return 3;
        else if(isword) return 2;
        else if(isprefix) return 1;
        else return 0;
    }


    // Same thing as before, but now we're doing it with a substring of the given string
    // Which isn't, like, an actual issue. Super easy
    public int searchPrefix(StringBuilder s, int a, int b){
        StringBuilder substring = new StringBuilder(s.substring(a, b));
        return searchPrefix(substring);
    }
}
