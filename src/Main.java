import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] arg) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        PhotosGeoFinder win = new PhotosGeoFinder();
        win.pack();
        win.setSize(1000, 800);
        win.setLocationRelativeTo(null);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setTitle("VK PHOTOS GEO FINDER");
        win.setVisible(true);
    }
}