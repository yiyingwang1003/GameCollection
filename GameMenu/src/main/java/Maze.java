import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Maze extends Game {
    public static final int W = 30, H = 20;
    public static final int xM = 50, yM = 50, c = 30;
    public static int[] next = new int[W + 1]; // pointer to next
    public static int[] prev = new int[W + 1]; // pointer back to prev
    public static int y;
    public static Graphics gg;


    public Maze() {
        super("Maze", 1000, 800);
    }

    public void paintComponent(Graphics g) { // called by operating system
        gg = g;
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, 5000, 5000);
        gg.setColor(Color.BLACK);
        G.RANDOM.setSeed(10); // make the maze static
        hRow0(); // the special top row
        mid(); // alternating between hRow and vRow
        vLast(); // last vertical
        hLast(); // last horizontal
    }

    private synchronized void hRow0() {
        y = yM;
        singletonCycle(0);
        for (int i = 0; i < W; i++) {
            singletonCycle(i + 1);
            hLine(i);
        }
    }

    public void hRule(int i) {
        if(!sameCycle(i, i+1) && pH()) {
            hLine(i);
        }
    }

    public void vRule(int i) {
        if(next[i] == i || pV()) { // means i only connects to itself // do not abandon this kind of lonely ones
            vLine(i);
        }else {
            noVLine(i);
        }
    }

    public void hRow() {
        for (int i = 0; i < W; i++) {
            hRule(i);
        }
    }

    public void vRow() {
        for (int i = 1; i < W; i++) {
            vRule(i);
        }
        vLine(0);
        vLine(W);
    }
    public void noVLine(int i) {split(i);}

    private void mid() {
        for(int i = 0; i < H - 1; i++) {
            vRow();
            y += c;
            hRow();
        }
    }

    private void hLast() {
        y += c;
        for (int i = 0; i < W; i++) {
            hLine(i);
        }
    }

    private void vLast() {
        vLine(0);
        vLine(W);
        for (int i = 1; i < W; i++) {
            if (!sameCycle(i , 0)) {
                merge(i, 0);
                vLine(i);
            }
        }
    }

    public int x(int i) {return xM + i * c;}

    public void hLine(int i) { // h :horizontal
        gg.drawLine(x(i), y, x(i + 1), y);
        merge(i, i + 1); // merge 2 lists together
    }
    public void vLine(int i) { // v : vertical
        gg.drawLine(x(i), y, x(i), y + c);
    }

    public void merge(int i, int j) {
        int iP = prev[i], jP = prev[j];
        next[iP] = j;
        next[jP] = i;
        prev[i] = jP;
        prev[j] = iP;
    }

    public void split(int i) {
        int iP = prev[i], iN = next[i];
        next[iP] = iN;
        prev[iN] = iP;
//        next[i] = i; // set i pointing to itself
//        prev[i] = i;
        singletonCycle(i);
    }

    public void singletonCycle(int i) {
        next[i] = i; // set i pointing to itself
        prev[i] = i;
    }

    public boolean sameCycle(int i, int j) {
        int n = next[i];
        while (n != i) {
            if (n == j) {return true;}
            n = next[n];
        }
        return false;
    }

    public static boolean pV() {return G.rnd(100) < 33;} // probability of making vertical connection // 33 : 33%
    public static boolean pH() {return G.rnd(100) < 33;} // probability of making horizontal connection // 33 : 33%


    @Override
    public void endGame() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

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
}