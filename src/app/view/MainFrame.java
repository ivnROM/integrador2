package app.view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import app.MainController;

public class MainFrame extends JFrame {
    private JPanel mainPanel; // panel principal generado por el designer
    private JButton resultButton;
    private MainController controller;

    public MainFrame(MainController controller) {
        this.controller = controller;
        mainPanel = new JPanel();
        setContentPane(mainPanel); // establece el panel diseñado como contenido
        setTitle("Aplicación de Sistemas de Ecuaciones");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // getter opcional para acceder al mainPanel si lo necesitás en el controlador
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
