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
        // el constructor inicializa los componentes visuales principales en la ventana
        setTitle("Sistema de Ecuaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputPanel = new InputPanel();
        resultPanel = new ResultPanel();

        languageSelector = new JComboBox<>(new String[]{"Español", "Português"});
        languageSelector.addActionListener(new LanguageChangeListener());

        methodSelector = new JComboBox<>(new String[]{"Cramer", "Gauss-Jordan"});

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(languageSelector, BorderLayout.EAST);
        topPanel.add(methodSelector, BorderLayout.WEST);

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

        add(topPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
        setSize(600, 180);
        setVisible(true);
    }

    // listener para ejecutar el cambio de idioma
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
        inputPanel.updateLanguage();
        resultPanel.updateLanguage();
    }

    // listener del boton de calcular con el patrón strategy
    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int equationCount = inputPanel.getEquationCount();
                int variableCount = inputPanel.getVariableCount();

                double[] solutions;
                String selectedMethod = (String) methodSelector.getSelectedItem();
                SolverStrategy solver;

                if ("Cramer".equals(selectedMethod)) {
                    if (equationCount == variableCount) {
                        Matrix coefficients = inputPanel.getCoefficientMatrix();
                        double[] constants = inputPanel.getConstants();
                        solver = new CramerSolver();
                        solutions = solver.solve(coefficients, constants);
                    } else {
                        // excepcion: las dimensiones no coinciden
                        throw new IllegalArgumentException("El método de Cramer requiere una matriz cuadrada.");
                    }
                } else {
                    // Método de Gauss-Jordan
                    Matrix augmentedMatrix = inputPanel.getAugmentedMatrix();
                    solver = new GaussJordanSolver();
                    solutions = solver.solve(augmentedMatrix);  // Llamada con la matriz aumentada
                }

                displayResultDialog(solutions);

            } catch (IllegalArgumentException ex) {
                // excepción: matrices no cuadradas o problemas con el cálculo
                JOptionPane.showMessageDialog(MainFrame.this, lenguaje.get("noSqrMatrix"), lenguaje.get("error"), JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                // excepcion: no hay solución para la ecuación presentada en el input
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
