package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import localization.MessageBundle;
import logic.Matrix;

public class InputPanel extends JPanel {
    private JTextField[][] coefficientFields;
    private JTextField[] constantFields;
    private JTextField equationCountField;
    private JTextField variableCountField;
    private JPanel matrixPanel;
    private JButton generateButton;
    JLabel contadorEcuaciones;
    JLabel contadorVariables;
    private MessageBundle lenguaje = MessageBundle.getInstance();


    public InputPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Campos para el número de ecuaciones y variables
        gbc.gridx = 0;
        gbc.gridy = 0;
        contadorEcuaciones = new JLabel(lenguaje.get("equationCount"));
        inputPanel.add(contadorEcuaciones, gbc);
        equationCountField = new JTextField(2);
        gbc.gridx = 1;
        inputPanel.add(equationCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contadorVariables = new JLabel(lenguaje.get("variableCount"));
        inputPanel.add(contadorVariables, gbc);
        variableCountField = new JTextField(2);
        gbc.gridx = 1;
        inputPanel.add(variableCountField, gbc);

        generateButton = new JButton(lenguaje.get("generateM"));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(generateButton, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Panel para la matriz de coeficientes y constantes
        matrixPanel = new JPanel();
        add(matrixPanel, BorderLayout.CENTER);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMatrixFields();
            }
        });
    }

    private void generateMatrixFields() {
        try {
            int rows = getEquationCount();
            int cols = getVariableCount();

            matrixPanel.removeAll();
            matrixPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);

            coefficientFields = new JTextField[rows][cols];
            constantFields = new JTextField[rows];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    coefficientFields[i][j] = new JTextField(5);
                    gbc.gridx = j;
                    gbc.gridy = i;
                    matrixPanel.add(coefficientFields[i][j], gbc);
                }
                constantFields[i] = new JTextField(5);
                gbc.gridx = cols;
                matrixPanel.add(constantFields[i], gbc);
            }

            revalidate();
            repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, lenguaje.get("invalidEq"), lenguaje.get("error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getEquationCount() throws NumberFormatException {
        return Integer.parseInt(equationCountField.getText());
    }

    public int getVariableCount() throws NumberFormatException {
        return Integer.parseInt(variableCountField.getText());
    }

    public Matrix getCoefficientMatrix() {
        int rows = getEquationCount();
        int cols = getVariableCount();
        double[][] coefficients = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                coefficients[i][j] = Double.parseDouble(coefficientFields[i][j].getText());
            }
        }
        return new Matrix(coefficients);
    }

    public double[] getConstants() {
        int rows = getEquationCount();
        double[] constants = new double[rows];
        for (int i = 0; i < rows; i++) {
            constants[i] = Double.parseDouble(constantFields[i].getText());
        }
        return constants;
    }

    public Matrix getAugmentedMatrix() {
        int rows = getEquationCount();
        int cols = getVariableCount();
        double[][] augmented = new double[rows][cols + 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                augmented[i][j] = Double.parseDouble(coefficientFields[i][j].getText());
            }
            augmented[i][cols] = Double.parseDouble(constantFields[i].getText());
        }
        return new Matrix(augmented);
    }

    public void clearInputs() {
        equationCountField.setText("");
        variableCountField.setText("");
        if (coefficientFields != null) {
            for (JTextField[] row : coefficientFields) {
                for (JTextField field : row) {
                    field.setText("");
                }
            }
        }
        if (constantFields != null) {
            for (JTextField field : constantFields) {
                field.setText("");
            }
        }
        matrixPanel.removeAll();
        revalidate();
        repaint();
    }

    public void updateLanguage() {
        contadorEcuaciones.setText(lenguaje.get("equationCount"));
        contadorVariables.setText(lenguaje.get("variableCount"));
        generateButton.setText(lenguaje.get("generateM"));
    }
}
