package app;

public class Main {
    public static void main(String[] args) {
        // lanzamos la aplicación dentro del hilo de eventos de swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainController controller = new MainController();
            controller.startApp();
        });
    }
}