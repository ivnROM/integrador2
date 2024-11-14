package logic;

public class CramerSolver implements SolverStrategy {
    @Override
    public double[] solve(Object... params) {
        if (params.length != 2) {
            throw new IllegalArgumentException("Se requieren dos parámetros: la matriz de coeficientes y el vector de constantes.");
        }

        Matrix coefficients = (Matrix) params[0];
        double[] constants = (double[]) params[1];

        int n = coefficients.getRowCount();

        // Determinante de la matriz principal
        double det = calculateDeterminant(coefficients.getData());

        if (det == 0) {
            throw new IllegalArgumentException("El sistema no tiene solución única (determinante = 0).");
        }

        // Array para las soluciones
        double[] solutions = new double[n];

        // Para cada incógnita, calcular el determinante de la matriz modificada
        for (int i = 0; i < n; i++) {
            double[][] modifiedMatrix = createModifiedMatrix(coefficients.getData(), constants, i);
            double detModified = calculateDeterminant(modifiedMatrix);
            solutions[i] = detModified / det;  // Xi = Det(Ai) / Det(A)
        }

        return solutions;
    }

    private static double[][] createModifiedMatrix(double[][] matrix, double[] constants, int column) {
        int n = matrix.length;
        double[][] modifiedMatrix = new double[n][n];

        // Copiar matriz original y reemplazar la columna indicada por los términos constantes
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                modifiedMatrix[i][j] = (j == column) ? constants[i] : matrix[i][j];
            }
        }
        return modifiedMatrix;
    }

    private static double calculateDeterminant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        } else if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            double det = 0;
            for (int j = 0; j < n; j++) {
                det += Math.pow(-1, j) * matrix[0][j] * calculateDeterminant(getMinor(matrix, 0, j));
            }
            return det;
        }
    }

    private static double[][] getMinor(double[][] matrix, int row, int column) {
        int n = matrix.length;
        double[][] minor = new double[n - 1][n - 1];
        int r = -1;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            r++;
            int c = -1;
            for (int j = 0; j < n; j++) {
                if (j == column) continue;
                minor[r][++c] = matrix[i][j];
            }
        }
        return minor;
    }
}
