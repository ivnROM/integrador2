import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends JFrame {

    private JTextField cantidadDeMallasTextField;
    private JButton continuarButton;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private JPanel panelMallas;

    private int cantidadDeMallas;
    private List<Malla> mallasList; // lista para almacenar las mallas
    private Set<String> intensidadesDisponibles; // conjunto para almacenar las intensidades únicas

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
        setLocationRelativeTo(null);
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
        mallasList = new ArrayList<>(); // inicializa la lista de mallas
        intensidadesDisponibles = new HashSet<>(); // inicializa el conjunto de intensidades

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
                        pack();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panelMallas, "Ingrese un número válido de componentes");
                    }
                }
            });

            mallaPanel.add(agregarComponentesButton);
            panelMallas.add(mallaPanel);
        }

        JButton calcularButton = new JButton("Calcular Intensidades");
        calcularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularIntensidades();
            }
        });

        panelMallas.add(calcularButton);
        mainPanel.add(panelMallas, "mallas");
    }

    private void agregarComponentes(JPanel mallaPanel, int cantidadComponentes, int mallaIndex) {
        mallaPanel.removeAll();

        JLabel mallaLabel = new JLabel("Malla " + (mallaIndex + 1) + ":");
        mallaPanel.add(mallaLabel);

        String[] columnNames = {"Componente", "Tipo", "Valor", "Corriente"};
        Object[][] data = new Object[cantidadComponentes][4];

        for (int j = 0; j < cantidadComponentes; j++) {
            data[j][0] = "Componente " + (j + 1);
            data[j][1] = "Fuente de energía";
            data[j][2] = "";
            data[j][3] = ""; // aquí dejaremos la corriente vacía inicialmente
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 3) {
                    String tipo = (String) getValueAt(row, 1);
                    return "Resistencia".equals(tipo);
                }
                return super.isCellEditable(row, column);
            }
        };

        JTable table = new JTable(model);

        String[] tipos = {"Fuente de energía", "Resistencia"};
        TableColumn tipoColumn = table.getColumnModel().getColumn(1);
        JComboBox<String> tipoComboBox = new JComboBox<>(tipos);
        tipoColumn.setCellEditor(new DefaultCellEditor(tipoComboBox));

        TableColumn valorColumn = table.getColumnModel().getColumn(2);
        valorColumn.setCellEditor(new DefaultCellEditor(new JTextField()));

        TableColumn corrienteColumn = table.getColumnModel().getColumn(3);
        JComboBox<String> corrienteComboBox = new JComboBox<>(getIntensidadesDisponibles());
        corrienteColumn.setCellEditor(new DefaultCellEditor(corrienteComboBox));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                return c;
            }
        });

        // Establecer el color de fondo para la celda de corriente
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) { // Solo modificar la celda de corriente
                    String tipo = (String) table.getValueAt(row, 1);
                    if ("Fuente de energía".equals(tipo)) {
                        c.setBackground(Color.GRAY); // Gris si es fuente de energía
                    } else {
                        c.setBackground(Color.WHITE); // Blanco si es resistencia
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, cantidadComponentes * 30));
        mallaPanel.add(scrollPane);

        Malla malla = new Malla(table, mallaIndex + 1); // crear la malla
        mallasList.add(malla); // agregar la malla a la lista

        mallaPanel.revalidate();
        mallaPanel.repaint();
        pack();
    }

    private String[] getIntensidadesDisponibles() {
        // Devolvemos las intensidades disponibles como un arreglo de Strings
        return intensidadesDisponibles.toArray(new String[0]);
    }

    private void calcularIntensidades() {
        for (Malla malla : mallasList) {
            malla.armarEcuacion(); // armar ecuación para cada malla
        }

        // resolver ecuaciones (requiere implementación)
        // mostrar los resultados
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

class Malla {
    private JTable table;
    private int numeroMalla;
    private List<Double> resistencias;
    private List<Double> fuentes;
    private double totalFuentes; // suma de todas las fuentes en la malla
    private double totalResistencias; // suma de todas las resistencias en la malla

    public Malla(JTable table, int numeroMalla) {
        this.table = table;
        this.numeroMalla = numeroMalla;
        this.resistencias = new ArrayList<>();
        this.fuentes = new ArrayList<>();
        this.totalFuentes = 0.0;
        this.totalResistencias = 0.0;
    }

    public void armarEcuacion() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        StringBuilder ecuacion = new StringBuilder();

        for (int row = 0; row < model.getRowCount(); row++) {
            String tipo = (String) model.getValueAt(row, 1);
            String valorStr = (String) model.getValueAt(row, 2);

            if (valorStr != null && !valorStr.isEmpty()) {
                try {
                    double valor = Double.parseDouble(valorStr);

                    if ("Resistencia".equals(tipo)) {
                        resistencias.add(valor);
                        ecuacion.append(" + ").append(valor).append(" * I").append(numeroMalla);
                    } else if ("Fuente de energía".equals(tipo)) {
                        fuentes.add(valor);
                        totalFuentes += valor;
                        ecuacion.append(" + ").append(valor);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Valor inválido en la tabla de componentes de la malla " + numeroMalla);
                }
            }
        }

        System.out.println("Ecuación de la malla " + numeroMalla + ": " + ecuacion.toString() + " = 0");
    }

    public List<Double> getResistencias() {
        return resistencias;
    }

    public List<Double> getFuentes() {
        return fuentes;
    }

    public double getTotalFuentes() {
        return totalFuentes;
    }

    public double getTotalResistencias() {
        return totalResistencias;
    }

    public int getNumeroMalla() {
        return numeroMalla;
    }
}
