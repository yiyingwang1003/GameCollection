import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SymPaint extends Game{
    public static final int W = 1000, H = 800;
    public static PolyLine currentLine;
    public static PolyLine.List all = new PolyLine.List();
    public static boolean debug = true;

    public SymPaint() {
        super("SymPaint", W, H);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 5000, 5000);
        g.setColor(Color.BLACK);
        all.show(g);
        if (debug) {
            g.setColor(Color.RED);
            String name = "r" + PolyLine.rVal + (PolyLine.isM ? "m" : "");
            name += PolyLine.isG ? (PolyLine.hg==0 ? "H" : "G") : "";
            g.drawString(name, 30, 50);
        }

        g.setColor(Color.ORANGE);
        String gStr = SGroup.isG ? SGroup.current().name : "Not group";
        g.drawString(gStr, 30, 30);
    }

    // -----------------------PolyLine-------------------------
    public static class PolyLine extends ArrayList<Point> {

        public static Point c = new Point(W/2, H/2); // center point
        public static Point a = new Point(), b = new Point(); // raw line segment
        // rotation
        public static Point ra = new Point(), rb = new Point(); // r - rotated a and b
        public static int rVal = 1; // r rotation count
        public static final double twoPi = Math.PI * 2;
        // mirror
        public static Point ma = new Point(), mb = new Point(); // m - mirror
        public static boolean isM = true;
        public static int md = 0; // displacement of vertical mirror
        // glide
        public static Point ga = new Point(), gb = new Point(); // g - glide
        public static boolean isG = true;
        public static int hg = 0; // horizontal extra translation // 0 - horizontal mirror // not 0 - glide
        // horizontal translation
        public static int hTLo, hTHi; // how many copies of horizontal // horizontal translation low // horizontal translation high
        public static Point hT = new Point(200, 0); // horizontal translation

        // vertical translation
        public static int vTLo, vTHi; // how many copies of vertical // vertical translation low // vertical translation high
        public static Point vT = new Point(0, 150); // vertical translation

        public static boolean isP3M1; // special case
        public static Point ka = new Point(), kb = new Point(); // special case for P3M1, P4G - crystallographer

        private void show(Graphics g) {
            setTransLimits();
            for (int i = 1; i < this.size(); i++) {
                a = get(i - 1); b = get(i); // get 2 adjacent points
                for (int iR = 0; iR < rVal; iR++) { // R - rotation
                    setR(iR); setM(); setG();

                    if (isM && isG) {setP4G();} // special case
                    if (isP3M1) {setP3M1();} // special case

                    for (int h = hTLo; h < hTHi; h++) {
                        for (int v = vTLo; v < vTHi; v++) {
                            int tx = h * hT.x + v * vT.x, ty = h * hT.y + v * vT.y; // t - translation
                            g.drawLine(ra.x + tx, ra.y + ty, rb.x + tx, rb.y + ty);

                            if (isM) {g.drawLine(ma.x + tx, ma.y + ty, mb.x + tx, mb.y + ty);}
                            if (isG) {g.drawLine(ga.x + tx, ga.y + ty, gb.x + tx, gb.y + ty);}

                            if (isM && isG) {g.drawLine(ka.x + tx, ka.y + ty, kb.x + tx, kb.y + ty);}  // special case
                            if (isP3M1) {g.drawLine(ka.x + tx, ka.y + ty, kb.x + tx, kb.y + ty);} // special case
                        }
                    }
                }
            }
        }
        // rotation
        private void setR(int iR) {
            double ith = iR * twoPi / rVal, cos = Math.cos(ith), sin = Math.sin(ith); // i'th / element in the list
            // rotation of i theta about the center point c
            ra.x = (int) ((a.x - c.x)*cos + (a.y - c.y)*sin + c.x);
            ra.y = (int) ((a.x - c.x)*(-sin) + (a.y - c.y)*cos + c.y);
            rb.x = (int) ((b.x - c.x)*cos + (b.y - c.y)*sin + c.x);
            rb.y = (int) ((b.x - c.x)*(-sin) + (b.y - c.y)*cos + c.y);
        }
        // mirror
        public void setM() {
            ma.x = 2*c.x + md - ra.x; ma.y = ra.y; // vertical does not change y
            mb.x = 2*c.x + md - rb.x; mb.y = rb.y;
        }

        // glide
        public void setG() {
            ga.x = ra.x + hg; ga.y = 2*c.y + md - ra.y;
            gb.x = rb.x + hg; gb.y = 2*c.y + md - rb.y;
        }

        public void setP4G() {
            ka.x = 2*c.x +md - ra.x; ka.y = 2*c.y + md - ra.y;
            kb.x = 2*c.x + md - rb.x; kb.y = 2*c.y + md - rb.y;
        }

        public void setP3M1() {
            ka.x = ra.x + hg; ka.y = 2*c.y + md - ra.y;
            kb.x = rb.x + hg; kb.y = 2*c.y + md - rb.y;
        }
        public void setTransLimits() {
            if (hT.x == 0 && hT.y == 0) {hTLo = 0; hTHi = 1;} else {hTLo = -6; hTHi = 6;}
            if (vT.x == 0 && vT.y == 0) {vTLo = 0; vTHi = 1;} else {vTLo = -10; vTHi = 10;}
        }

        public void add(int x, int y) {add(new Point(x, y));}

        // -----------------------List-------------------------
        public static class List extends ArrayList<PolyLine> {

            public void show(Graphics g) {
                for (PolyLine pl : this) {
                    pl.show(g);
                }
            }
        }

    }

    // -------------------- SGroup-----------------------
    public static class SGroup {
        public static final double DZ = 0, DX = 200, DY = 150, R3 = Math.sqrt(3) / 2;
        public static ArrayList<SGroup> groups = new ArrayList<SGroup>();
        public static int gID = 0; // group ID
        public static boolean isG; // is a group

        // translation patterns
        public static double[] tP = {0, 0, 0, 0};// t - translation P - point
        public static double[] tS = {0, 0, DX, 0};// t - translation S - strip
        public static double[] tW = {0, DY, DX, 0};// t - translation  W - wall
        public static double[] tW4 = {0, DX, DX, 0};// t - translation  W - wall rotation is 4
        public static double[] tW36 = {DX / 2, DX * R3, DX, 0};// t - translation  W - wall rotation is 3/6
        public static double[] t3M1 = {DX * 1.5, DX * R3, 0, 2 * R3 * DX};// t - translation // special case
        public static double[] tCM = {DX / 2, DY, DX, 0};// t - translation // special case
        public static double[] tCMM = {DX, DY, DX, -DY};// t - translation // special case

        // glide & mirror patterns
        public static double[] g0 = {0, 0};// g - glide // no mirror no glide , the normal one
        public static double[] gA = {DX / 2, 0};// g - glide
        public static double[] gGG = {DX / 2, DY / 2};// g - glide in 2 different directions
        public static double[] g4G = {0, DX / 2};// g - glide

        // create all the groups
        static {
            // point groups
            for (int r = 1; r < 21; r++) {
                new SGroup("R" + r, r, g0, tP); // do not have mirror
                (new SGroup("D" + r, r, g0, tP)).m = true; // have mirror
            }
            // strip groups
            new SGroup("P111", 1, g0, tS);
            new SGroup("P112", 2, g0, tS);
            (new SGroup("PM11", 1, g0, tS)).m = true;
            (new SGroup("PM12", 2, g0, tS)).m = true;
            (new SGroup("P1M1", 1, g0, tS)).g = true;
            (new SGroup("P1A1", 1, gA, tS)).g = true;
            (new SGroup("PMA2", 2, gA, tS)).g = true;

            // wallpaper
            (new SGroup("P1", 1, g0, tW)).txy[0] = DZ;
            (new SGroup("PM", 1, g0, tW)).m = true; // mirror
            (new SGroup("PG", 1, gA, tW)).g = true; // glide
            (new SGroup("CM", 1, g0, tCM)).g = true; // glide
            new SGroup("P2", 2, g0, tW);
            (new SGroup("PMM", 2, g0, tW)).m = true; // mirror
            (new SGroup("PGG", 2, gGG, tW)).g = true; // glide
            (new SGroup("PMG", 2, gA, tW)).g = true; // glide
            (new SGroup("CMM", 2, g0, tCMM)).m = true; // mirror
            new SGroup("P3", 3, g0, tW36);
            (new SGroup("P3M1", 3, g0, t3M1)).isP3M1 = true;
            (new SGroup("P31M", 3, g0, tW36)).g = true;
            new SGroup("P4", 4, g0, tW4);
            SGroup sg = new SGroup("P4G", 4, g4G, tW4); sg.m = true; sg.g = true;
            (new SGroup("P4M", 4, g0, tW4)).m = true;
            new SGroup("P6", 6, g0, tW36);
            (new SGroup("P6M", 6, g0, tW36)).m = true;


        }

        public String name;
        public int r; // rotation
        public boolean m = false, g = false, isP3M1 = false; // mirror glide P3M1
        public double[] gm; // glide & mirror
        public double[] txy; // x translation y translation

        public SGroup(String name, int r, double[] gm, double[] txy) {
            this.name = name; this.r = r; this.gm = gm; this.txy = txy;
            groups.add(this);
        }

        public void set() {
            PolyLine.isM = m; PolyLine.isG = g; PolyLine.isP3M1 = isP3M1;
            PolyLine.rVal = r;
            PolyLine.hg = (int) gm[0];
            PolyLine.md = (int) gm[1];
            PolyLine.hT.x = (int) txy[2]; PolyLine.hT.y = (int) txy[3];
            PolyLine.vT.x = (int) txy[0]; PolyLine.vT.y = (int) txy[1];

        }
        public static SGroup current() {return groups.get(gID);}

        public static void left() {
            SGroup group = (gID == 0) ? groups.get(0) : groups.get(--gID);
            group.set();
        }

        public static void right() {
            int n = groups.size() - 1;
            SGroup group = (gID == n) ? groups.get(n) : groups.get(++gID);
            group.set();
        }
    }

    @Override
    public void endGame() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int vK = e.getKeyCode();
        boolean isG = false; // is group or not
        boolean isBK = false;
        if (vK == KeyEvent.VK_UP) {if (PolyLine.rVal < 20) {PolyLine.rVal++;}}
        if (vK == KeyEvent.VK_DOWN) {if (PolyLine.rVal > 0) {PolyLine.rVal--;}}
        if (vK == KeyEvent.VK_BACK_SPACE) {all.clear(); currentLine = null; isBK = true;}
        if (vK == KeyEvent.VK_LEFT) {SGroup.left(); isG = true;}
        if (vK == KeyEvent.VK_RIGHT) {SGroup.right(); isG = true;}

        char ch = e.getKeyChar();
        // mirror
        if (ch == 'M' || ch == 'm') {PolyLine.isM = !PolyLine.isM;}
        // horizontal mirror
        if (ch == 'H' || ch == 'h') {PolyLine.isG = !PolyLine.isG; PolyLine.hg = 0;}
        // glide
        if (ch == 'G' || ch == 'g') {PolyLine.isG = true; PolyLine.hg = PolyLine.hT.x / 2;}

        if (!isBK) {
            SGroup.isG = isG; // set isG
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentLine = new PolyLine();
        currentLine.add(e.getX(), e.getY());
        all.add(currentLine);
        repaint();
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
        currentLine.add(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
