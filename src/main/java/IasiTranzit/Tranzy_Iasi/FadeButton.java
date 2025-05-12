package IasiTranzit.Tranzy_Iasi;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class FadeButton extends JButton {
    private static final long serialVersionUID = 1L;
    private float alpha = 1.0f;

    public FadeButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    //BUTON CU FADE, functia pare ok nu e incarcata
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (this.alpha < 1.0f) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        boolean originalContentAreaFilled = isContentAreaFilled();
        boolean originalBorderPainted = isBorderPainted();
        setContentAreaFilled(true);
        setBorderPainted(true);

        super.paintComponent(g2d);

        setContentAreaFilled(originalContentAreaFilled);
        setBorderPainted(originalBorderPainted);

        g2d.dispose();
    }

    //probabil e pentru bara de sus, nu sunt sigur
    @Override
    public Dimension getPreferredSize() {
         boolean ocf = isContentAreaFilled();
         boolean obp = isBorderPainted();
         setContentAreaFilled(true);
         setBorderPainted(true);
         Dimension size = super.getPreferredSize();
         setContentAreaFilled(ocf);
         setBorderPainted(obp);
         return size;
    }

    @Override
    public boolean contains(int x, int y) {
        return alpha > 0.1f && super.contains(x, y);
    }
}
