package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import localization.MessageBundle;
import logic.CramerSolver;
import logic.GaussJordanSolver;
import logic.Matrix;
import logic.SolverStrategy;

public class MainFrame extends JFrame {
    private InputPanel inputPanel;
    private ResultPanel resultPanel;
    private JComboBox<String> languageSelector;
    private JComboBox<String> methodSelector;
    private JButton calculateButton;
    private JButton clearButton;
    private MessageBundle lenguaje = MessageBundle.getInstance();

    public MainFrame() {
        setTitle("Sistema de Ecuaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de Entrada y Panel de Resultados
        inputPanel = new InputPanel();
        resultPanel = new ResultPanel();

        // Selector de idioma
        languageSelector = new JComboBox<>(new String[]{"Español", "Português"});
        languageSelector.addActionListener(new LanguageChangeListener());

        // Selector de método
        methodSelector = new JComboBox<>(new String[]{"Cramer", "Gauss-Jordan"});

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(languageSelector, BorderLayout.EAST);
        topPanel.add(methodSelector, BorderLayout.WEST);

        // Botones
        calculateButton = new JButton(lenguaje.get("calculate"));
        clearButton = new JButton(lenguaje.get("clear"));

        calculateButton.addActionListener(new CalculateButtonListener());
        clearButton.addActionListener(e -> {
            inputPanel.clearInputs();
            resultPanel.clearResults();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);

        // Añadir componentes al JFrame
        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
        setSize(600, 180);
        setVisible(true);
    }

    private class LanguageChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedLanguage = (String) languageSelector.getSelectedItem();
            lenguaje.setLanguage(selectedLanguage.equals("Español") ? "es" : "pt");
            updateLanguage();
        }
    }

    private void updateLanguage() {
        setTitle(lenguaje.get("title"));
        calculateButton.setText(lenguaje.get("calculate"));
        clearButton.setText(lenguaje.get("clear"));
        inputPanel.updateLanguage();  // llamar a updateLanguage de InputPanel
        resultPanel.updateLanguage(); // llamar a updateLanguage de ResultPanel
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Obtener la cantidad de ecuaciones y variables
                int equationCount = inputPanel.getEquationCount();
                int variableCount = inputPanel.getVariableCount();

                double[] solutions;
                String selectedMethod = (String) methodSelector.getSelectedItem();
                SolverStrategy solver;

                // Verificar el método seleccionado
                if ("Cramer".equals(selectedMethod)) {
                    if (equationCount == variableCount) {
                        // Método de Cramer: obtener la matriz de coeficientes y el vector de constantes
                        Matrix coefficients = inputPanel.getCoefficientMatrix();
                        double[] constants = inputPanel.getConstants();
                        solver = new CramerSolver();
                        solutions = solver.solve(coefficients, constants);  // Llamada con los parámetros correspondientes
                    } else {
                        // Si las dimensiones no coinciden, lanzar una excepción
                        throw new IllegalArgumentException("El método de Cramer requiere una matriz cuadrada.");
                    }
                } else { // Método de Gauss-Jordan
                    // Obtener la matriz aumentada para Gauss-Jordan
                    Matrix augmentedMatrix = inputPanel.getAugmentedMatrix();
                    solver = new GaussJordanSolver();
                    solutions = solver.solve(augmentedMatrix);  // Llamada con la matriz aumentada
                }

                // Mostrar los resultados en un nuevo diálogo
                displayResultDialog(solutions);

            } catch (IllegalArgumentException ex) {
                // Excepción para matrices no cuadradas o problemas con el cálculo
                JOptionPane.showMessageDialog(MainFrame.this, lenguaje.get("noSqrMatrix"), lenguaje.get("error"), JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                // Manejo de excepciones generales (otros posibles errores en la resolución)
                JOptionPane.showMessageDialog(MainFrame.this, lenguaje.get("noSolution"), lenguaje.get("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void displayResultDialog(double[] solutions) {
        StringBuilder resultText = new StringBuilder(lenguaje.get("results")).append(":\n");
        for (int i = 0; i < solutions.length; i++) {
            resultText.append(lenguaje.get("variable")).append(" ").append(i + 1).append(" = ").append(solutions[i]).append("\n");
        }

        JTextArea resultArea = new JTextArea(resultText.toString());
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(240, 255, 240));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, lenguaje.get("results"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
