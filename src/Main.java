import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Componente {
    String tipo;
    double valor;
    int corriente;

    Componente(String tipo, double valor, int corriente) {
        this.tipo = tipo;
        this.valor = valor;
        this.corriente = corriente;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ingrese el número de mallas: ");
        int numMallas = scanner.nextInt();
        scanner.nextLine(); // limpiar el buffer

        // lista para guardar componentes por malla
        List<List<Componente>> mallas = new ArrayList<>();

        // proceso de ingreso de datos para cada malla
        for (int i = 0; i < numMallas; i++) {
            System.out.println("\n--- malla " + (i + 1) + " ---");
            List<Componente> componentesMalla = new ArrayList<>();
            int corriente = i + 1;

            while (true) {
                System.out.print("¿agregar componente? (si/no): ");
                String respuesta = scanner.nextLine();
                if (respuesta.equalsIgnoreCase("no")) break;

                System.out.print("tipo de componente (fuente/resistencia): ");
                String tipo = scanner.nextLine();

                if (tipo.equalsIgnoreCase("fuente")) {
                    System.out.print("ingrese el voltaje de la fuente (positivo o negativo): ");
                    double voltaje = scanner.nextDouble();
                    scanner.nextLine(); // limpiar el buffer
                    componentesMalla.add(new Componente("fuente", voltaje, 0));
                } else if (tipo.equalsIgnoreCase("resistencia")) {
                    System.out.print("ingrese el valor de la resistencia: ");
                    double resistencia = scanner.nextDouble();
                    scanner.nextLine(); // limpiar el buffer
                    System.out.println("corriente asignada a esta resistencia: i" + corriente);
                    componentesMalla.add(new Componente("resistencia", resistencia, corriente));
                } else {
                    System.out.println("componente no reconocido, intente nuevamente.");
                }
            }

            mallas.add(componentesMalla);
        }

        // procesamiento de las ecuaciones usando la ley de kirchhoff
        double[][] coeficientes = new double[numMallas][numMallas];
        double[] constantes = new double[numMallas];

        for (int i = 0; i < numMallas; i++) {
            List<Componente> componentes = mallas.get(i);
            for (Componente componente : componentes) {
                if (componente.tipo.equals("fuente")) {
                    constantes[i] -= componente.valor; // agregamos voltaje a la ecuación de la malla
                } else if (componente.tipo.equals("resistencia")) {
                    coeficientes[i][componente.corriente - 1] += componente.valor; // suma resistencia a la corriente correspondiente
                }
            }
        }

        // resolver el sistema de ecuaciones
        double[] corrientes = resolverSistema(coeficientes, constantes);

        // mostrar resultados
        System.out.println("\nresultados:");
        for (int i = 0; i < numMallas; i++) {
            System.out.printf("i%d = %.2f a\n", i + 1, corrientes[i]);
        }

        scanner.close();
    }

    // método para resolver el sistema usando eliminación gaussiana
    public static double[] resolverSistema(double[][] coeficientes, double[] constantes) {
        int n = constantes.length;

        for (int i = 0; i < n; i++) {
            // hacemos que el elemento de la diagonal sea 1 dividiendo la fila
            double factor = coeficientes[i][i];
            for (int j = 0; j < n; j++) {
                coeficientes[i][j] /= factor;
            }
            constantes[i] /= factor;

            // hacemos ceros en la columna actual en filas distintas a la actual
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    factor = coeficientes[k][i];
                    for (int j = 0; j < n; j++) {
                        coeficientes[k][j] -= factor * coeficientes[i][j];
                    }
                    constantes[k] -= factor * constantes[i];
                }
            }
        }

        return constantes; // las constantes son ahora las corrientes
    }
}
