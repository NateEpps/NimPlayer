import java.io.*;
import java.util.*;

public class TreeNimPlayer
{
    public static boolean ShouldPrintScores = true;
    public static boolean ShouldShuffleChoices = false;
    
    private static class NimNode implements Comparable<NimNode>
    {
        private int[] piles;
        private boolean maximizing;
        private ArrayList<NimNode> children;
        private NimNode parent;
        
        public NimNode(int i1, int i2, int i3)
        {
            this.piles = new int[3];
            piles[0] = i1;
            piles[1] = i2;
            piles[2] = i3;
            
            this.maximizing = true;
            this.children = null;
            this.parent = null;
        }
        
        private NimNode()
        {
            piles = null;
            maximizing = false;
            children = null;
            parent = null;
        }
        
        public ArrayList<NimNode> expand()
        {
            if (children != null)
                return children;
            
            children = new ArrayList<>();
            if (isTerminal())
                return children;
            
            for (int pNo = 0; pNo < 3; pNo++) {
                for (int sub = 1; sub <= piles[pNo]; sub++) {
                    int[] newArr = piles.clone();
                    newArr[pNo] -= sub;
                    NimNode newNode = makeChild(newArr);
                    children.add(makeChild(newArr));
                }
            }
            
            Collections.sort(children);
            return children;
        }
        
        private NimNode makeChild(int[] newArr)
        {
            NimNode node = new NimNode();
            node.piles = newArr;
            node.maximizing = !this.maximizing;
            node.parent = this;
            
            return node;
        }
        
        public boolean isTerminal()
        {
            return piles[0] == 0 && piles[1] == 0 && piles[2] == 0;
        }
        
        public boolean isMaximizing()
        {
            return maximizing;
        }
        
        public int score()
        {
            if (isTerminal()) {
                if (maximizing)
                    return 1;
                else
                    return -1;
            }
            
            else {
                int total = 0;
                for (NimNode child : expand())
                    total += child.score();
                return total;
            }
        }
        
        @Override
        public int compareTo(NimNode that)
        {
            return this.score() - that.score();
        }
        
        @Override
        public String toString()
        {
            String self = Arrays.toString(piles);
            if (maximizing)
                self = "(+) " + self;
            else
                self = "(-) " + self;
            
            if (ShouldPrintScores)
                self += " (" + score() + ")";
            
            return self;
        }
        
        public String toLongString()
        {
            return toString() + " " + Arrays.toString(expand().toArray());
        }
        
        private void printGameNotLast(PrintStream os)
        {
            if (parent != null)
                parent.printGameNotLast(os);
            
            os.print(toString() + " => ");
        }
        
        public void printGame(PrintStream os)
        {
            if (parent != null)
                parent.printGameNotLast(os);
            
            os.print(toString() + "\n");
        }
    }
    
    public static void main(String[] args)
    {
        int p1, p2, p3;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Enter initial piles: ");
                p1 = scanner.nextInt();
                p2 = scanner.nextInt();
                p3 = scanner.nextInt();
                if (p1 < 0 || p2 < 0 || p3 < 0)
                    throw new IllegalArgumentException("");
                break;
            }
            catch (Exception e) {
                continue;
            }
        }
        
        NimNode node = new NimNode(p1, p2, p3);
        boolean userWins = true;
        
        while (true) {
            /* user */
            System.out.println("Current Board: " + node.toString() + "\n");
            
            ArrayList<NimNode> children = node.expand();
            if (ShouldShuffleChoices)
                Collections.shuffle(children);
            
            for (int x = 0; x < children.size(); x++)
                System.out.println("\t" + x + ")\t" + children.get(x));
            System.out.println();
            
            int user = -1;
            while (user < 0 || user >= children.size()) {
                System.out.print("What is your choice? ");
                try {
                    user = scanner.nextInt();
                }
                catch (Exception ex) {
                    
                }
            }
            
            node = children.get(user);
            System.out.println("You chose " + node + "\n");
            if (node.isTerminal()) {
                userWins = false;
                break;
            }
            
            /* computer */
            children = node.expand();
            
            node = children.get(0);
            
            System.out.println("Computer chooses " + node + "\n");
            
            if (node.isTerminal()) {
                userWins = true;
                break;
            }
        }
        
        if (userWins)
            System.out.println("*** You Win! ***\n");
        else
            System.out.println("*** You lose ***\n");
        
        System.out.print("Game path: ");
        node.printGame(System.out);
    }
}
