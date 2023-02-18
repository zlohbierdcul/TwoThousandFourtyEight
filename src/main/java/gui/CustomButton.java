package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class CustomButton extends JButton {
    public ButtonStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        if (this.style != style) {
            this.style = style;
            currentStyle.changeStyle(style);
        }
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        repaint();
    }

    private ButtonStyle style = ButtonStyle.PRIMARY;
    private ButtonColor currentStyle = new ButtonColor(ButtonStyle.PRIMARY);
    private Color backgroundColor;
    private int round = 10;

    public CustomButton(String s) {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setForeground(Color.WHITE);
        setSize(30, 25);
        setText(s);
        backgroundColor = currentStyle.background;
    }

    public CustomButton(String s, Color c) {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setForeground(Color.WHITE);
        setSize(30, 25);
        setText(s);
        this.backgroundColor = c;

    }


    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 0;
        int y = 0;
        int width = getWidth();
        int height = getHeight();
        Area area = new Area(new RoundRectangle2D.Double(x, y, width, height, round, round));
        g2.setColor(backgroundColor);
        g2.fill(area);
        area.subtract(new Area(new RoundRectangle2D.Double(x, y, width, height - 2, round, round)));
        g2.setColor(backgroundColor);
        g2.fill(area);
        g2.dispose();
        super.paintComponent(grphcs);
    }

    public enum ButtonStyle {
        PRIMARY(new Color(223, 194, 26), new Color(51, 51, 51), new Color(193, 181, 70), new Color(234, 225, 121)),
        SECONDARY(new Color(203, 209, 219), new Color(58, 70, 81), new Color(81, 92, 108), new Color(230, 239, 255)),
        DESTRUCTIVE(new Color(255, 138, 48), new Color(238, 238, 238), new Color(198, 86, 0), new Color(255, 161, 90));

        private ButtonStyle(Color background, Color foreground, Color backgroundHover, Color backgroundPress) {
            this.background = background;
            this.foreground = foreground;
            this.backgroundHover = backgroundHover;
            this.backgroundPress = backgroundPress;
        }
        private Color background;
        private Color foreground;
        private Color backgroundHover;
        private Color backgroundPress;

        public void setBackground(Color background) {
            this.background = background;
        }
    }

    protected class ButtonColor {

        public Color getBackground() {
            return background;
        }

        public void setBackground(Color background) {
            this.background = background;
        }

        public Color getForeground() {
            return foreground;
        }

        public void setForeground(Color foreground) {
            this.foreground = foreground;
        }

        public Color getBackgroundHover() {
            return backgroundHover;
        }

        public void setBackgroundHover(Color backgroundHover) {
            this.backgroundHover = backgroundHover;
        }

        public Color getBackgroundPress() {
            return backgroundPress;
        }

        public void setBackgroundPress(Color backgroundPress) {
            this.backgroundPress = backgroundPress;
        }

        public ButtonColor(ButtonStyle style) {
            changeStyle(style);
        }

        public ButtonColor() {
        }

        private Color background;
        private Color foreground;
        private Color backgroundHover;
        private Color backgroundPress;

        private void changeStyle(ButtonStyle style) {
            this.background = style.background;
            this.foreground = style.foreground;
            this.backgroundHover = style.background;
            this.backgroundPress = style.backgroundPress;
        }
    }

}
