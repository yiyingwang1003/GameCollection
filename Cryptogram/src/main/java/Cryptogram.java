
import java.awt.Color;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class Cryptogram extends Window{
    public static final int dCode = 18, dGuess = 40,
                            xM = 50, yM = 50, // M is margin
                            lineGap = 10,
                            W = 15, H = 45;
    public static G.V SPACE = new G.V(W, 0), // space the chars
                      START = new G.V(xM, yM),
                      NL = new G.V(0, lineGap + H); // NL = new line

    public static Cell.List cells = new Cell.List();
    public static Font font = new Font("Verdana", Font.PLAIN, 20);

    public Cryptogram() {
        super("Cryptogram", 1000, 800);
//        // test
//        Cell c = new Cell(Pair.alpha[0]);
//        c.p.guess = "B";
//        cells.add(c);
//        cells.add(new Cell(Pair.alpha[3]));
//        Cell.newLine();
//        cells.add(new Cell(Pair.alpha[5]));
//        Cell.selected = c;
        loadQuote("NOW IS THE TIME FOR ALL GOOD MEN");
    }

    public void paintComponent(Graphics g) {
        G.whiteBackground(g);
        g.setFont(font);
        cells.show(g);
    }

    public void loadQuote(String str) {
        // str = validate(str) // remove punctuations, change to uppercase
        cells.clear();
//        Pair.shuffle();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int iAlpha = c - 'A';
            if (c >= 'A' && c <= 'Z') {
                new Cell(Pair.alpha[iAlpha]);
            } else {
                Cell.space();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me){
        int x = me.getX(), y = me.getY();
        Cell.selected = cells.hit(x, y);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        if (c >= 'a' && c <= 'z') {c = (char)(c - 'a' + 'A');} // convert to uppercase
        if (Cell.selected != null) {
            Cell.selected.p.guess = (c >= 'A' && c <= 'Z') ? "" + c: "";
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int vk = ke.getKeyCode(); // virtual key
        if (Cell.selected != null) {
            if (vk == KeyEvent.VK_LEFT) {Cell.selected.left();}
            if (vk == KeyEvent.VK_RIGHT) {Cell.selected.right();}
            repaint();
        }
        }


    public static void main (String[] args) {
        Window.PANEL = new Cryptogram();
        Window.launch();
    }

    //---------------Pair--------------
    public static class Pair {
        public char actual, code;
        public String guess;
        public static Pair[] alpha = new Pair[26];
        static {
            for (int i = 0; i < 26; i++) {
                alpha[i] = new Pair((char)('A'+i)); // cast int to char
            }
        }

        public Pair(char c) {
            this.actual = c;
            this.code = c;
            this.guess = "";
        }
    }

    //-----------Cell------------------
    public static class Cell {
        public Pair p;
        public int ndx;
        public G.V loc = new G.V();
        public static G.V nextLoc = new G.V(START);
        public static G.V nextLine = new G.V(START);
        public static G.VS vs = new G.VS(0, 0, W, H);
        public static Cell selected = null;

        public static void newLine(){
            nextLine.add(NL);
            nextLoc.set(nextLine);
        }

        public Cell(Pair pair) {
            this.p = pair;
            ndx = cells.size();
            cells.add(this);
            this.loc.set(nextLoc);
            space();
        }

        public static void space() {nextLoc.add(SPACE);}

        public void show(Graphics g) {
            if (this.equals(Cell.selected)) {
                vs.loc.set(this.loc);
                vs.draw(g, Color.red);
            }

            g.setColor(Color.black);
            g.drawString("" + this.p.code, this.loc.x, this.loc.y + dCode); // turn char into string
            g.drawString(this.p.guess, this.loc.x, this.loc.y + dGuess);
        }

        public boolean hit(int x, int y) {
            vs.loc.set(this.loc);
            return vs.hit(x, y);
        }

        public void left() {if (ndx > 0) {Cell.selected = cells.get(ndx - 1);}}
        public void right() {if (ndx < cells.size() - 1) {Cell.selected = cells.get(ndx + 1);}}


        //-----------Cell.List-----------
        public static class List extends ArrayList<Cell> {
            public void show(Graphics g) {for (Cell c : this) {c.show(g); }}
            public Cell hit (int x, int y) {
                for (Cell c : this) {
                    if (c.hit(x, y)) {return c;}
                }
                return null;
            }
        }
    }
    //
}
