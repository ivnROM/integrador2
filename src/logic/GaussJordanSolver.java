package logic;

public class GaussJordanSolver implements SolverStrategy {
    @Override
    public double[] solve(Object... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("GaussJordanSolver requiere un solo parámetro (la matriz aumentada).");
        }

        Matrix augmentedMatrix = (Matrix) params[0];  // obtenemos la matriz aumentada de los parámetros
        int n = augmentedMatrix.getRowCount();
        double[] solutions = new double[n];

        for (int i = 0; i < n; i++) {
            double pivot = augmentedMatrix.get(i, i);
            if (pivot == 0) throw new IllegalArgumentException("El sistema no tiene solución única.");
            // dividimos la fila por un pivote para normalizar la diagonal
            for (int j = 0; j < n + 1; j++) {
                augmentedMatrix.set(i, j, augmentedMatrix.get(i, j) / pivot);
            }
            // se eliminan las demás filas con el pivote actual
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = augmentedMatrix.get(k, i);
                for (int j = 0; j < n + 1; j++) {
                    augmentedMatrix.set(k, j, augmentedMatrix.get(k, j) - factor * augmentedMatrix.get(i, j));
                }
            }
        }

        // asignamos las soluciones (que son la última columna de la matriz aumentada)
        for (int i = 0; i < n; i++) {
            solutions[i] = augmentedMatrix.get(i, n);
        }

        return solutions;  // Devolvemos el arreglo con las soluciones
    }
}
