import GraphicsLib.Window;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

import java.util.ArrayList;
import java.util.Random;

public class Snake extends Window implements ActionListener{
    public static Random RND = new Random();
    public static int rnd(int k) {return RND.nextInt(k);}

    public static Timer timer;
    public static int W = 40, H = 30, C = 20, Xoff = 100, Yoff = 100;
    public static final int snakeSize = 3;
    public static Chain chain = new Chain(snakeSize);

    public static char turn = 'a'; // L - left  R - right  else - straight
    public static boolean gameOver = false;

    public static final int initialTime = 200;
    public static int timeToDeath = initialTime;
    public static int foodValue = 100;
    public static Point food = new Point(rnd(W), rnd(H));
    public static Color foodColor = Color.PINK;


    public Snake() {
        super("SNAKE", 1000, 1000);
        timer = new Timer(100, this);
        timer.start();
    }

    public void fillCell(Graphics g, Point p, Color color){
        g.setColor(color);
        g.fillRect(p.x * C + Xoff, p.y * C + Yoff, C, C);
    }

    public void paintComponent(Graphics g) {
        timeToDeath--;
        if (timeToDeath == 0) {gameOver = true;}
        // fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 5000, 5000);
        g.setColor(Color.BLACK);
        g.drawRect(Xoff, Yoff, W*C, H*C);
        g.drawString("Time: " + timeToDeath, 50, 50);
        // draw the rectangle
        g.setColor(Color.RED);
        // g.fillRect(100 + rnd(600), 100 + rnd(600), 100, 100);
        chain.draw(g);
        // chain.step();
        // if (rnd(3) == 0) { // one-third of the time
        //     if (rnd(2) == 0) {
        //         chain.left();
        //     } else {
        //         chain.right();
        //     }
        // }
        
        fillCell(g, food, foodColor);;

        if (!gameOver) {
            chain.step();
            if (turn == 'L') {chain.left(); turn = 'a';}
            if (turn == 'R') {chain.right(); turn = 'a';}
        }else {  //game is over
           chain.drawDeadhead(g);
        }
        
    }


    @Override
    public void actionPerformed(ActionEvent e) {repaint();}

    @Override
    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        if (c == ' ') {startOver(); return;}
        if (c =='k') {turn = 'L'; return;}
        if (c =='j') {turn = 'R'; return;}
        turn = 'a';
    }

    private void startOver() {
        chain = new Chain(snakeSize);
        gameOver = false;
        timeToDeath = initialTime;
    }

    public static class Chain extends ArrayList<Point> {
        int head;
        Point dH = new Point(1, 0); // deltaH
        public Chain(int n) {
            int N = n;
            head = 0;
            int x = rnd(W), y = rnd(H);
            for (int i = 0; i < N; i++) {
                add(new Point(x, y));
            }
        }

        public void draw(Graphics g) {
            for (int i = 0; i < size(); i++) {
                Point p = get(i);
                g.fillRect(p.x * C + Xoff, p.y * C + Yoff, C, C); // need fillCell
            }
        }

        public void drawDeadhead(Graphics g) {
            g.setColor(Color.YELLOW);
            Point p = get(head);
            g.fillRect(p.x * C + Xoff, p.y * C + Yoff, C, C);
        }

        public void step() {
            int tail = (head + 1) % size();
            Point t = get(tail), h = get(head);
            t.x = h.x + dH.x;
            t.y = h.y + dH.y;
            head = tail;
            detectCollision();
            detectFood();
        }

        private void detectFood() {
            Point h = get(head);
            if (h.x == food.x && h.y == food.y) {
                timeToDeath += foodValue;
                // make the snake longer
                chain.add(new Point(h.x, h.y));

                // create new food
                food.x = rnd(W);
                food.y = rnd(H);
            }
        }

        public void left() {
            int t = dH.x; dH.x = dH.y; dH.y = t;  // swap x and y
            dH.x *= -1;
        }

        public void right() {
            int t = dH.x; dH.x = dH.y; dH.y = t; // swap x and y
            dH.y *= -1;
        }

        public void detectCollision() {
            Point h = get(head);
            // wall collision
            if (h.x < 0 || h.y < 0 || h.x >= W || h.y >= H) {gameOver = true;}

            // self collision
            for (Point p : this) {
                if (p != h) {
                    if (p.x == h.x && p.y == h.y) {gameOver = true;}
                }
            }
        }

    }
}
