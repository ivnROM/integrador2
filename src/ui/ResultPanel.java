package ui;

import javax.swing.*;
import localization.MessageBundle;

public class ResultPanel extends JPanel {
    private JTextArea resultArea;
    private MessageBundle lenguaje = MessageBundle.getInstance();

    public ResultPanel() {
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea));
    }

    public void displayResults(double[] solutions) {
        StringBuilder results = new StringBuilder();
        for (int i = 0; i < solutions.length; i++) {
            results.append(lenguaje.get("variable")).append(" ").append(i + 1).append(": ").append(solutions[i]).append("\n");
        }
        resultArea.setText(results.toString());
    }

    public void displayError(String errorMessage) {
        resultArea.setText(errorMessage);
    }

    public void clearResults() {
        resultArea.setText("");
    }

    public void updateLanguage() {
        // Si querés traducir el contenido mostrado, actualizá el mensaje de cada variable en el área de resultados
        String currentText = resultArea.getText();
        if (!currentText.isEmpty()) {
            String[] lines = currentText.split("\n");
            StringBuilder updatedText = new StringBuilder();
            for (String line : lines) {
                if (line.startsWith("variable")) {
                    String[] parts = line.split(":");
                    updatedText.append(lenguaje.get("variable")).append(parts[0].substring(8)).append(":").append(parts[1]).append("\n");
                } else {
                    updatedText.append(line).append("\n");
                }
            }
            resultArea.setText(updatedText.toString());
        }
    }
}
