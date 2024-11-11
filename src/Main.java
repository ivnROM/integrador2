import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private JTextField cantidadDeMallasTextField;
    private JButton continuarButton;
    private JButton confirmacionButton; // Botón de confirmación
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private JPanel panelMallas;

    private int cantidadDeMallas;

    public Main() {
        setTitle("Calculadora Kirchhoff");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initPanelInicio();

        mainPanel.add(panelInicio, "inicio");
        add(mainPanel);

        cardLayout.show(mainPanel, "inicio");
        pack();
        setLocationRelativeTo(null); // centra la ventana
    }

    private void initPanelInicio() {
        panelInicio = new JPanel();
        panelInicio.setLayout(new FlowLayout());

        JLabel label = new JLabel("Ingresá la cantidad de mallas:");
        cantidadDeMallasTextField = new JTextField(10);
        continuarButton = new JButton("Continuar");

        continuarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continuarButtonSubmit();
            }
        });

        panelInicio.add(label);
        panelInicio.add(cantidadDeMallasTextField);
        panelInicio.add(continuarButton);
    }

    private void initPanelMallas() {
        panelMallas = new JPanel();
        panelMallas.setLayout(new BoxLayout(panelMallas, BoxLayout.Y_AXIS));

        for (int i = 0; i < cantidadDeMallas; i++) {
            JPanel mallaPanel = new JPanel();
            mallaPanel.setLayout(new FlowLayout());

            JLabel mallaLabel = new JLabel("Malla " + (i + 1) + ":");
            JTextField cantidadComponentesTextField = new JTextField(5);
            JLabel componentesLabel = new JLabel("Cantidad de componentes:");

            mallaPanel.add(mallaLabel);
            mallaPanel.add(componentesLabel);
            mallaPanel.add(cantidadComponentesTextField);

            JButton agregarComponentesButton = new JButton("Agregar Componentes");
            int mallaIndex = i; // para capturar el índice de la malla
            agregarComponentesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int cantidadComponentes = Integer.parseInt(cantidadComponentesTextField.getText());
                        agregarComponentes(mallaPanel, cantidadComponentes, mallaIndex);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panelMallas, "Ingrese un número válido de componentes");
                    }
                }
            });

            mallaPanel.add(agregarComponentesButton);
            panelMallas.add(mallaPanel);
        }

        // Botón de confirmación único al final
        confirmacionButton = new JButton("Confirmar");
        confirmacionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción de confirmación, puedes agregar lo que necesites aquí
                JOptionPane.showMessageDialog(panelMallas, "Componentes confirmados.");
            }
        });
        panelMallas.add(confirmacionButton); // Agregar el botón al final de la ventana

        // Actualiza el tamaño de la ventana cada vez que cambie el contenido
        mainPanel.add(panelMallas, "mallas");
        pack();
    }

    private void agregarComponentes(JPanel mallaPanel, int cantidadComponentes, int mallaIndex) {
        mallaPanel.removeAll();

        // título de la malla, no editable
        JLabel mallaLabel = new JLabel("Malla " + (mallaIndex + 1) + ":");
        mallaPanel.add(mallaLabel);

        // definir las columnas de la tabla
        String[] columnNames = {"Componente", "Tipo", "Valor", "Corriente"};
        Object[][] data = new Object[cantidadComponentes][4];

        // llenamos los datos con los componentes de cada malla
        for (int j = 0; j < cantidadComponentes; j++) {
            data[j][0] = "Componente " + (j + 1); // nombre del componente
            data[j][1] = "Fuente de energía"; // valor inicial en la columna tipo
            data[j][2] = ""; // valor inicial en la columna valor
            data[j][3] = "";  // columna de corriente vacía
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo se puede editar la celda de "Corriente" si la columna "Tipo" es "Resistencia"
                if (column == 3) { // columna de Corriente
                    String tipo = (String) getValueAt(row, 1); // valor de la columna "Tipo"
                    return "Resistencia".equals(tipo); // Solo editable si es Resistencia
                }
                return super.isCellEditable(row, column); // por defecto, otras celdas sí son editables
            }
        };

        JTable table = new JTable(model);

        // Cambiar el renderizador para la columna "Corriente" para establecer color
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String tipo = (String) table.getValueAt(row, 1); // valor de la columna "Tipo"
                if ("Fuente de energía".equals(tipo)) {
                    c.setBackground(Color.GRAY); // Si el tipo es Fuente de energía, poner fondo gris
                } else {
                    c.setBackground(Color.WHITE); // Si es Resistencia, poner fondo blanco
                }
                return c;
            }
        });

        // Configurar la columna "Tipo" con un combo box
        String[] tipos = {"Fuente de energía", "Resistencia"};
        TableColumn tipoColumn = table.getColumnModel().getColumn(1);
        JComboBox<String> tipoComboBox = new JComboBox<>(tipos);
        tipoColumn.setCellEditor(new DefaultCellEditor(tipoComboBox));

        // Configurar la columna "Valor" con un editor de texto
        TableColumn valorColumn = table.getColumnModel().getColumn(2);
        valorColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

        // Modificar la lista de intensidades dependiendo de las mallas
        TableColumn corrienteColumn = table.getColumnModel().getColumn(3);
        corrienteColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(getIntensidades(mallaIndex))));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, cantidadComponentes * 30));
        mallaPanel.add(scrollPane);

        mallaPanel.revalidate();
        mallaPanel.repaint();

        // Actualiza el tamaño de la ventana cada vez que se agregan componentes
        panelMallas.invalidate();
        panelMallas.revalidate();
        pack(); // ajusta el tamaño de la ventana según el contenido
    }


    private String[] getIntensidades(int mallaIndex) {
        // Aquí se deben retornar las intensidades correspondientes por malla
        String[] intensidades = new String[cantidadDeMallas];
        for (int i = 0; i < cantidadDeMallas; i++) {
            intensidades[i] = "I" + (i + 1);  // Asignamos las intensidades de las mallas
        }
        return intensidades;
    }

    private void continuarButtonSubmit() {
        try {
            String text = cantidadDeMallasTextField.getText();
            cantidadDeMallas = Integer.parseInt(text);

            if (panelMallas != null) {
                mainPanel.remove(panelMallas);
            }

            initPanelMallas();
            cardLayout.show(mainPanel, "mallas");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panelInicio, "Por favor, ingrese un número válido de mallas");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
