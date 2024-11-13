package logic;

public class GaussJordanSolver {
    public static double[] solve(Matrix augmentedMatrix) {
        int n = augmentedMatrix.getRowCount();
        double[] solutions = new double[n];

        for (int i = 0; i < n; i++) {
            double pivot = augmentedMatrix.get(i, i);
            if (pivot == 0) throw new IllegalArgumentException("El sistema no tiene solución única.");
            for (int j = 0; j < n + 1; j++) {
                augmentedMatrix.set(i, j, augmentedMatrix.get(i, j) / pivot);
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = augmentedMatrix.get(k, i);
                for (int j = 0; j < n + 1; j++) {
                    augmentedMatrix.set(k, j, augmentedMatrix.get(k, j) - factor * augmentedMatrix.get(i, j));
                }
            }
        }
        for (int i = 0; i < n; i++) {
            solutions[i] = augmentedMatrix.get(i, n);
        }
        return solutions;
    }
}
