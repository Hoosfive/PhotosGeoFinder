import com.vk.api.sdk.client.actors.UserActor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PhotosGeoFinder extends JFrame {

    private JPanel panel1;
    private JTextField latitudeTF;
    private JTextField longitudeTF;
    private JTextField radiusTF;
    private JTable resultTable;
    private JButton searchBtn;
    private JTextField countTF;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JToolBar toolBar;
    private Vector<MyPhoto> photosList;
    private final DefaultTableModel model = new DefaultTableModel();
    private final String[] columnNames = {"ID владельца", "Страница владельца", "Фото", "Фото (гр. = club)", "Широта", "Долгота"};


    PhotosGeoFinder() {
        this.getContentPane().add(panel1);
        initToolbar();
        initListeners();
        UserActor actor = Config.readConfig();
        if (actor == null || actor.getId() == null || actor.getId() == 0 || actor.getAccessToken() == null)
            GetPhotos.changeToken();
    }

    private void initListeners() {
        searchBtn.addActionListener(e -> {
            statusLabel.setText("В процессе...");
            getPhotos();
        });
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultTable.rowAtPoint(evt.getPoint());
                int col = resultTable.columnAtPoint(evt.getPoint());
                if ((col == 1 || col == 2 || col == 3) && resultTable.getValueAt(row, col) != null) {
                    openHyperLink(resultTable.getValueAt(row, col).toString());
                }
            }
        });
    }

    private void getPhotos() {
        progressBar.setIndeterminate(true);


        String latitude = latitudeTF.getText();
        String longitude = longitudeTF.getText();
        String raduis = radiusTF.getText();
        int count = Integer.parseInt(countTF.getText());
        photosList = new Vector<>();
        Callable getPhotos = new GetPhotos(latitude, longitude, raduis, count);
        FutureTask<Vector<MyPhoto>> future = new FutureTask<Vector<MyPhoto>>(getPhotos);

        Runnable thr = () -> {
            new Thread(future).start();
            try {
                photosList = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            statusLabel.setText("Выполнено! Результатов: " + photosList.size());
            progressBar.setIndeterminate(false);
            fillTable();
        };
        Thread runThr = new Thread(thr);
        runThr.start();
    }

    private void fillTable() {
        model.setColumnCount(0);
        model.setRowCount(0);
        model.setColumnIdentifiers(columnNames);
        photosList.forEach(el -> model.addRow(new String[]{String.valueOf(el.ownerID),
                el.ownerUrl,
                el.photoURl,
                el.groupPhotoUrl,
                String.valueOf(el.latitude),
                String.valueOf(el.longitude)}));
        resultTable.setModel(model);

    }

    private void openHyperLink(String urlToOpen) {
        URI uri = null;
        try {
            uri = new URI(urlToOpen);
        } catch (URISyntaxException e1) {
            System.out.println("Неправильная ссылка: " + uri);
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(uri);
        } catch (IOException e2) {
            JOptionPane.showMessageDialog(null,
                    "Браузер не был обнаружен.");
        }
    }

    private void initToolbar() {
        JButton openLinks = new JButton("Открыть несколько ссылок");
        toolBar.add(openLinks);
        openLinks.addActionListener(e -> {
            String indexes = JOptionPane.showInputDialog(null,
                    new String[]{"Введите индекс начала и конца списка:", "Например '\"30,45\"' откроет с 30 по 45 ссылку"},
                    "Автооткрытие ссылок",
                    JOptionPane.WARNING_MESSAGE);
            openHyperlinkSublist(indexes);
        });
    }

    private void openHyperlinkSublist(String indexes) {
        int start = Integer.parseInt(indexes.substring(0, indexes.indexOf(",")));
        int end = Integer.parseInt(indexes.substring(indexes.indexOf(",") + 1));
        Vector sublist = new Vector();
        for (int i = start - 1; i <= end - 1; i++) {
            sublist.add(resultTable.getValueAt(i, 2));
        }
        sublist.forEach(el -> openHyperLink(el.toString()));
    }
}

