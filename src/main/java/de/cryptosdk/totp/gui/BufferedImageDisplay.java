package de.cryptosdk.totp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class BufferedImageDisplay {
    public static void show(InputStream pictureBytes) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TOTP as QR-Code");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            try {
                BufferedImage image = ImageIO.read(pictureBytes);
                JPanel imagePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (image != null) {
                            g.drawImage(image, 0, 0, this);
                        }
                    }
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(image.getWidth(), image.getHeight());
                    }
                };
                frame.add(imagePanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
