package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named("idiomaModel")
@SessionScoped
public class IdiomaModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String locale = "es";

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {

        this.locale = locale;

        Locale nuevoLocale = switch (locale) {

            case "en" ->
                Locale.US;

            case "zh" ->
                Locale.TAIWAN;

            default ->
                new Locale("es");
        };

        FacesContext.getCurrentInstance()
                .getViewRoot()
                .setLocale(nuevoLocale);
    }

    /**
     * Locale que debe utilizar cada nueva vista.
     */
    public Locale getCurrentLocale() {

        return switch (locale) {

            case "en" ->
                Locale.US;

            case "zh" ->
                Locale.TAIWAN;

            default ->
                new Locale("es");
        };
    }
}
