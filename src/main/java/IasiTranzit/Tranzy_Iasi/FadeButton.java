package IasiTranzit.Tranzy_Iasi;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

/**
 * Clasa {@code FadeButton} extinde {@code JButton} pentru a implementa un buton cu efect de fade.
 * Permite controlul transparentei butonului.
 */
public class FadeButton extends JButton {
	
	/** Serial version UID generat automat pentru clasa JButton */
    private static final long serialVersionUID = 1L;
    
    /**
     * Reprezinta nivelul de transparenta al butonului.
     * Valoarea 1.0f inseamna complet opac, iar 0.0f inseamna complet transparent.
     */
    private float alpha = 1.0f;

    /**
     * Creeaza o noua instanta de {@code FadeButton} cu textul specificat.
     * Configureaza butonul sa fie transparent si fara chenar.
     * @param text Textul afisat pe buton.
     */
    public FadeButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    /**
     * Seteaza valoarea de transparenta (alpha) a butonului.
     * Valoarea alpha trebuie sa fie intre 0.0f (complet transparent) si 1.0f (complet opac).
     * @param alpha Valoarea de transparenta.
     */
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        repaint();
    }

    /**
     * Returneaza valoarea curenta de transparenta (alpha) a butonului.
     * @return Valoarea de transparenta.
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Redeseneaza componenta, aplicand efectul de transparenta.
     * @param g Obiectul {@code Graphics} folosit pentru desenare.
     */
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
    
    /**
     * Calculeaza dimensiunea preferata a butonului, tinand cont de setarile interne temporare.
     * @return Dimensiunea preferata a butonului.
     */
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

    /**
     * Verifica daca un punct specificat se afla in interiorul butonului, tinand cont de transparenta.
     * Butonul nu este considerat "activ" pentru clicuri daca transparenta este prea mare.
     * @param x Coordonata X a punctului.
     * @param y Coordonata Y a punctului.
     * @return {@code true} daca punctul se afla in interiorul butonului si transparenta este suficient de mica,
     * {@code false} altfel.
     */
    @Override
    public boolean contains(int x, int y) {
        return alpha > 0.1f && super.contains(x, y);
    }
}