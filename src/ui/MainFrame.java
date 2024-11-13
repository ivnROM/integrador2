package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import localization.MessageBundle;
import logic.CramerSolver;
import logic.GaussJordanSolver;
import logic.Matrix;

public class MainFrame extends JFrame {
    private InputPanel inputPanel;
    private ResultPanel resultPanel;
    private JComboBox<String> languageSelector;
    private JComboBox<String> methodSelector;
    private JButton calculateButton;
    private JButton clearButton;

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
        calculateButton = new JButton(MessageBundle.get("calculate"));
        clearButton = new JButton(MessageBundle.get("clear"));

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
            MessageBundle.setLanguage(selectedLanguage.equals("Español") ? "es" : "pt");
            updateLanguage();
        }
    }

    private void updateLanguage() {
        setTitle(MessageBundle.get("title"));
        calculateButton.setText(MessageBundle.get("calculate"));
        clearButton.setText(MessageBundle.get("clear"));
        inputPanel.updateLanguage();  // llamar a updateLanguage de InputPanel
        resultPanel.updateLanguage(); // llamar a updateLanguage de ResultPanel
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int equationCount = inputPanel.getEquationCount();
                int variableCount = inputPanel.getVariableCount();

                double[] solutions;
                String selectedMethod = (String) methodSelector.getSelectedItem();

                if ("Cramer".equals(selectedMethod)) {
                    if (equationCount == variableCount) {
                        Matrix coefficients = inputPanel.getCoefficientMatrix();
                        double[] constants = inputPanel.getConstants();
                        solutions = CramerSolver.solve(coefficients, constants);
                    } else {
                        throw new IllegalArgumentException("El método de Cramer requiere una matriz cuadrada.");
                    }
                } else { // Gauss-Jordan
                    Matrix augmentedMatrix = inputPanel.getAugmentedMatrix();
                    solutions = GaussJordanSolver.solve(augmentedMatrix);
                }

                // Mostrar los resultados en un nuevo diálogo
                displayResultDialog(solutions);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, MessageBundle.get("noSqrMatrix"), MessageBundle.get("error"), JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, MessageBundle.get("noSolution"), MessageBundle.get("error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayResultDialog(double[] solutions) {
        StringBuilder resultText = new StringBuilder(MessageBundle.get("results")).append(":\n");
        for (int i = 0; i < solutions.length; i++) {
            resultText.append(MessageBundle.get("variable")).append(" ").append(i + 1).append(" = ").append(solutions[i]).append("\n");
        }

        JTextArea resultArea = new JTextArea(resultText.toString());
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(240, 255, 240));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, MessageBundle.get("results"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
