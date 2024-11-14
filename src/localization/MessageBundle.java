package localization;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class MessageBundle {
    private static MessageBundle instance;
    private ResourceBundle bundle;

    private MessageBundle(Locale locale) {
        bundle = ResourceBundle.getBundle("localization.messages", locale);
    }

    public static MessageBundle getInstance() {
        if (instance == null) {
            instance = new MessageBundle(new Locale("es", "ES"));
        }
        return instance;
    }

    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.out.println("La clave \"" + key + "\" no existe en el archivo de propiedades.");
            return "!" + key + "!";
        }
    }

    public void setLanguage(String languageCode) {
        try {
            Locale locale = new Locale(languageCode);
            bundle = ResourceBundle.getBundle("localization.messages", locale);
            System.out.println("Idioma cambiado a: " + locale.getLanguage());
        } catch (MissingResourceException e) {
            System.out.println("No se encontró el archivo de propiedades para el idioma: " + languageCode);
        }
    }
}
