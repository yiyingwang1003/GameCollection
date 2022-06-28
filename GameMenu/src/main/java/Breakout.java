import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Breakout extends Game implements ActionListener {

    private static final int H = 16, W = 50, PW = 100, nBrick = 13, PV = 15; // P-paddle
    private static final int LEFT = 100, RIGHT = LEFT + nBrick * W, TOP = 50, BOT = 700;
    public static final int MAXLIFE = 3;
    public static final int GAP = 3 * H;

    public static int lives, score, rowCount;
    public static Paddle paddle = new Paddle();
    public static Ball ball = new Ball();

    public Timer timer;

    public Breakout() {
        super("Breakout", 1000, 800);
        timer = new Timer(30, this);
        timer.start();
        startGame();
    }

    public void paintComponent(Graphics g) {
        G.whiteBackground(g);
        g.setColor(Color.BLACK);
        g.fillRect(LEFT, TOP, RIGHT - LEFT, BOT - TOP);
        g.drawString("Lives: " + lives, LEFT + 20, 30);
        g.drawString("Score: " + score, RIGHT - 80, 30);

        paddle.show(g);
        ball.show(g);
        Brick.List.ALL.show(g);
    }

    @Override
    public void endGame() {
        timer.stop();
        timer = null;
    }

    public static void startGame() {
        lives = MAXLIFE;
        score = 0;
        rowCount = 0;
        startNewRows();
    }

    public static void startNewRows() {
        rowCount++;
        Brick.List.ALL.clear();
        Brick.newBrickRows(rowCount);
        ball.init();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int vK = ke.getKeyCode();
        if (vK == KeyEvent.VK_LEFT) {paddle.left();}
        if (vK == KeyEvent.VK_RIGHT) {paddle.right();}
        if (ke.getKeyChar() == ' ') {paddle.dxStuck = -1;}
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ball.move();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // --------------------Brick--------------------

    public static class Brick extends G.VS {
        public static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.ORANGE};
        public Color color;

        public Brick(int x, int y) {
            super(x, y, W, H);
            color = colors[G.rnd(colors.length)];
            Brick.List.ALL.add(this);
        }

        public void show(Graphics g) {fill(g, color); draw(g, Color.BLACK);}

        public boolean hit (int x, int y) {
            return (x < loc.x + W) && (x + H > loc.x) && (y > loc.y) && (y < loc.y + H);
        }

        public void destroy() {
            ball.dy = -ball.dy;
            List.ALL.remove(this);
            score += 17;
            if (List.ALL.isEmpty()) {startNewRows();}
        }

        public static void newBrickRows (int n) { // n-how many rows
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < nBrick; j++) {
                    new Brick(LEFT + j * W, TOP + GAP + i * H);
                }
            }
        }

        //----------List of bricks------------
        public static class List extends ArrayList<Brick> {
            public static List ALL = new List();

            public void show(Graphics g) {for (Brick brick : this) {brick.show(g);}}

            public static void ballHitBrick() {
                int x = ball.loc.x, y = ball.loc.y;
                for (Brick brick : ALL) { // this??
                    if (brick.hit(x, y)) {brick.destroy();return;}
                }
            }
        }
    }

    // --------------------Ball---------------------
    public static class Ball extends G.VS {
        private static final int DY_START = -11;
        public Color color = Color.WHITE;
        public int dx = 11, dy = -DY_START;
        public Ball() {
            super(LEFT, BOT - 2*H, H, H);
        }
        public void init() {
            paddle.dxStuck = PW / 2 - H / 2; // center of the paddle
            loc.set(paddle.loc.x + paddle.dxStuck, BOT - 2 * H);
            dx = 0; dy = DY_START;
        }
        public void show(Graphics g) {fill(g, color);}

        public void move() {
            if (paddle.dxStuck < 0) {
                loc.x += dx; loc.y += dy;
                wallBounce();
                Brick.List.ballHitBrick();
            }
        }

        public void wallBounce() {
            if (loc.x < LEFT) {loc.x = LEFT; dx = -dx;}
            if (loc.x > RIGHT - H) {loc.x = RIGHT - H; dx = -dx;}
            if (loc.y < TOP) {loc.y = TOP; dy = -dy;}
            if (loc.y > BOT - 2*H) {paddle.hitBall();}
        }
    }


    // ---------------------Paddle------------------
    public static class Paddle extends G.VS {
        public Color color = Color.YELLOW;
        public int dxStuck = 10;
        public Paddle() {
            super(LEFT, BOT - H, PW, H);
        }

        public void show(Graphics g) {fill(g, color);}

        public void left() { loc.x += -PV; limitX();}

        public void right() { loc.x += PV; limitX();}

        public void limitX() {
            if (loc.x < LEFT) {loc.x = LEFT;}
            if (loc.x > RIGHT - PW) {loc.x = RIGHT - PW;}
            if (dxStuck >= 0) {ball.loc.set(loc.x + dxStuck, BOT - 2*H);}
        }

        public void hitBall() {
            if (ball.loc.x < loc.x || ball.loc.x > loc.x + PW) {
                // ball does not hit the paddle, life lost
                lives--;
                if (lives == 0) {startGame();}
                else { ball.init();}

            } else {
                // ball bounce
                ball.dy = -ball.dy;
                ball.dx += dxAdjust();
            }
        }
        public int dxAdjust() {
            int cP = paddle.loc.x + PW / 2; // center
            return (ball.loc.x + H / 2 - cP) / 10; // 10 can be changed to make it more sensitive

        }
    }

}
