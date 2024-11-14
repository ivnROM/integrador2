package logic;

public class Matrix {
    private double[][] data;

    // primer constructor que recibe el número de filas y columnas
    public Matrix(int rows, int columns) {
        data = new double[rows][columns];
    }

    // segunda constructor que inicializa la matriz con un array bidimensional
    public Matrix(double[][] coefficients) {
        data = coefficients;
    }

    // devuelve el valor de un elemento en la matriz
    public double get(int row, int column) {
        return data[row][column];
    }

    // establece un valor en una posición específica de la matriz
    public void set(int row, int column, double value) {
        data[row][column] = value;
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return data[0].length;
    }

    // devuelve los datos de la matriz (para compatibilidad con la clase de CramerSolver)
    public double[][] getData() {
        return data;
    }

    // esto clona la matriz
    public Matrix clone() {
        Matrix clone = new Matrix(getRowCount(), getColumnCount());
        for (int i = 0; i < getRowCount(); i++) {
            System.arraycopy(data[i], 0, clone.data[i], 0, getColumnCount());
        }
        return clone;
    }

    // esto calcula el determinante (usando Laplace para matrices mayores a 3x3)
    public double determinant() {
        int n = getRowCount();
        if (n == 1) return data[0][0];
        if (n == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        double det = 0;
        for (int i = 0; i < n; i++) {
            det += Math.pow(-1, i) * data[0][i] * subMatrix(0, i).determinant();
        }
        return det;
    }

    // este obtiene una submatriz excluyendo una fila y columna específicas
    private Matrix subMatrix(int excludingRow, int excludingColumn) {
        Matrix subMatrix = new Matrix(getRowCount() - 1, getColumnCount() - 1);
        for (int i = 0, r = 0; i < getRowCount(); i++) {
            if (i == excludingRow) continue;
            for (int j = 0, c = 0; j < getColumnCount(); j++) {
                if (j == excludingColumn) continue;
                subMatrix.set(r, c++, data[i][j]);
            }
            r++;
        }
        return subMatrix;
    }
}
