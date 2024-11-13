package localization;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class MessageBundle {
    private static ResourceBundle bundle = ResourceBundle.getBundle("localization.messages", new Locale("es", "ES"));

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.out.println("La clave \"" + key + "\" no existe en el archivo de propiedades.");
            return "!" + key + "!";
        }
    }

    public static void setLanguage(String languageCode) {
        try {
            // permite configurar el idioma y el país (opcional)
            Locale locale = new Locale(languageCode);
            bundle = ResourceBundle.getBundle("localization.messages", locale);
            System.out.println("Idioma cambiado a: " + locale.getLanguage());
        } catch (MissingResourceException e) {
            System.out.println("No se encontró el archivo de propiedades para el idioma: " + languageCode);
        }
    }
}
