import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseMovement extends JFrame {
    private JPanel panel;
    private int ufoX = 0;
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(100, 50);

    public MouseMovement() {
        ImageIcon originalSpaceBackground = new ImageIcon("src/Space_Background2.png");
        Image resizedImage = originalSpaceBackground.getImage().getScaledInstance(StandardFrame.screenWidth, StandardFrame.screenHeight, Image.SCALE_SMOOTH);
        ImageIcon spaceBackground = new ImageIcon(resizedImage);
        JLabel label = new JLabel();
        label.setIcon(spaceBackground);
        setContentPane(label);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.gray);
                g.fillOval(ufoX, ufoY, ufoSize.width, ufoSize.height);
            }
        };

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ufoX = e.getX() - ufoSize.width / 2;
                ufoY = e.getY() - ufoSize.height / 2;
                panel.repaint();
            }
        });
        panel.setOpaque(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(StandardFrame.frameSize);
        StandardFrame.setFrameMiddle(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MouseMovement();
    }
}
