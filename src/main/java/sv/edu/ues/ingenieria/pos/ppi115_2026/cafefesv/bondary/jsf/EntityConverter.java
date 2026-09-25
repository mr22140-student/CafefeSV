package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.jsf;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.util.Map;
import java.util.UUID;

@FacesConverter(value = "entityConverter", managed = true)
public class EntityConverter implements Converter<Object> {

    private static final String ATTRIBUTE_KEY = "entityConverter_";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        // Recupera la instancia guardada en la memoria del componente
        return component.getAttributes().get(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        Map<String, Object> attributes = component.getAttributes();

        // Busca si el objeto ya tiene una clave asignada
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getKey().startsWith(ATTRIBUTE_KEY) && value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        // Si es nuevo en la vista, genera un ID temporal y guarda la entidad en el componente
        String key = ATTRIBUTE_KEY + UUID.randomUUID().toString();
        attributes.put(key, value);
        return key;
    }
}