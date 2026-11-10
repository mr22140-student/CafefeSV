package sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.bondary.rest;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.ingenieria.pos.ppi115_2026.cafefesv.control.AbstractDAO;

/**
 * CRUD REST generico. Cada recurso (RolResource, CajaResource...) hereda de
 * esta clase y solo agrega lo propio de su entidad, igual que los models
 * heredan de AbstractModel.
 *
 * <pre>
 * GET    /recurso?first=0&amp;max=50  lista paginada (header Total-Registros)
 * GET    /recurso/{id}              un registro
 * POST   /recurso                   crea (201 + header Location)
 * PUT    /recurso/{id}              modifica (hay que mandar el objeto completo)
 * DELETE /recurso/{id}              elimina (204)
 * </pre>
 *
 * Codigos: 200 ok, 201 creado, 204 eliminado, 404 no existe, 422 dato invalido
 * o regla de negocio (header Wrong-Parameter con el campo y el mensaje en el
 * cuerpo), 500 error del servidor (header App-Error).
 *
 * Las reglas de negocio NO se escriben aqui: los recursos llaman a los
 * validarXxx de sus DAOs, los mismos que usan los models de JSF.
 *
 * @author daniel
 */
public abstract class AbstractResource<Entidad> {

    private static final Logger LOGGER = Logger.getLogger(AbstractResource.class.getName());

    /**
     * Maximo de registros por pagina en el GET de la lista.
     */
    protected static final int MAX_POR_PAGINA = 100;

    /**
     * Convierte el JSON que llega en el cuerpo (POST y PUT) a la entidad.
     */
    private static final Jsonb JSONB = JsonbBuilder.create();

    @Inject
    protected Validator validator;

    private final Class<Entidad> claseEntidad;

    protected AbstractResource(Class<Entidad> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }

    // ===================== Lo que cada recurso debe implementar =====================
    /**
     * El DAO de la entidad.
     */
    protected abstract AbstractDAO<Entidad, UUID> dao();

    /**
     * Lee la llave primaria del registro (por ejemplo r.getIdRol()).
     */
    protected abstract UUID getId(Entidad registro);

    /**
     * Escribe la llave primaria del registro (por ejemplo r.setIdRol(id)).
     */
    protected abstract void setId(Entidad registro, UUID id);

    // ===================== Ganchos opcionales (por defecto no hacen nada) =====================
    /**
     * Cambia cada relacion que llega solo con su id, por ejemplo
     * {"idCaja":{"idCaja":"..."}}, por la entidad real de la base. Usar
     * {@link #referencia}.
     */
    protected void resolverRelaciones(Entidad registro) {
    }

    /**
     * Valores por defecto al CREAR (estado inicial, fecha, activo...).
     */
    protected void prepararNuevo(Entidad registro) {
    }

    /**
     * Limpieza al crear Y al modificar (quitar espacios, valores vacios...).
     */
    protected void normalizar(Entidad registro) {
    }

    /**
     * Reglas para crear. Devuelve el mensaje de error o null.
     */
    protected String validarCrear(Entidad nuevo) {
        return null;
    }

    /**
     * Reglas para modificar. "guardado" es como esta el registro en la base
     * ANTES del cambio (reemplaza al estadoOriginal de los models).
     */
    protected String validarModificar(Entidad nuevo, Entidad guardado) {
        return null;
    }

    /**
     * Reglas para eliminar. Devuelve el mensaje de error o null.
     */
    protected String validarEliminar(Entidad guardado) {
        return null;
    }

    /**
     * Efectos despues de modificar (por ejemplo, Pago cambia el estado de la
     * factura).
     */
    protected void despuesDeModificar(Entidad actualizado, Entidad guardado) {
    }

    /**
     * Como se elimina. Por defecto delete del DAO; Empleado lo cambia.
     */
    protected void eliminar(Entidad guardado) {
        dao().delete(guardado);
    }

    // ===================== Endpoints =====================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(@QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {
        if (first < 0 || max < 1 || max > MAX_POR_PAGINA) {
            return error422("first,max", "first debe ser >= 0 y max debe estar entre 1 y " + MAX_POR_PAGINA + ".");
        }
        try {
            // OJO: el findRange de AbstractDAO recibe (inicio, fin), no (inicio, cantidad)
            List<Entidad> lista = dao().findRange(first, first + max);
            return Response.ok(lista, MediaType.APPLICATION_JSON)
                    .header("Total-Registros", dao().count())
                    .build();
        } catch (Exception ex) {
            return manejar(ex, "first,max");
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") String id) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        try {
            Entidad encontrado = dao().findById(uuid);
            if (encontrado == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(encontrado, MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, "id");
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(String cuerpo, @Context UriInfo uriInfo) {
        Entidad registro = leer(cuerpo);
        if (registro == null) {
            return error422(nombreRecurso(), "El cuerpo no es un JSON valido de " + claseEntidad.getSimpleName() + ".");
        }
        try {
            if (getId(registro) == null) {
                setId(registro, UUID.randomUUID());
            } else if (dao().findById(getId(registro)) != null) {
                return error422("id", "Ya existe un registro con ese id.");
            }
            resolverRelaciones(registro);
            prepararNuevo(registro);
            normalizar(registro);
            validarAnotaciones(registro);
            String error = validarCrear(registro);
            if (error != null) {
                return error422(nombreRecurso(), error);
            }
            dao().save(registro);
            URI ubicacion = uriInfo.getAbsolutePathBuilder().path(getId(registro).toString()).build();
            return Response.created(ubicacion).entity(registro).type(MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, nombreRecurso());
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizar(@PathParam("id") String id, String cuerpo) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        Entidad registro = leer(cuerpo);
        if (registro == null) {
            return error422(nombreRecurso(), "El cuerpo no es un JSON valido de " + claseEntidad.getSimpleName() + ".");
        }
        try {
            Entidad guardado = dao().findById(uuid);
            if (guardado == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            setId(registro, uuid); // manda el id de la URL, no el del cuerpo
            resolverRelaciones(registro);
            normalizar(registro);
            validarAnotaciones(registro);
            String error = validarModificar(registro, guardado);
            if (error != null) {
                return error422(nombreRecurso(), error);
            }
            Entidad actualizado = dao().update(registro);
            despuesDeModificar(actualizado, guardado);
            return Response.ok(actualizado, MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, nombreRecurso());
        }
    }

    @DELETE
    @Path("{id}")
    public Response eliminarPorId(@PathParam("id") String id) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        try {
            Entidad guardado = dao().findById(uuid);
            if (guardado == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            String error = validarEliminar(guardado);
            if (error != null) {
                return error422(nombreRecurso(), error);
            }
            eliminar(guardado);
            return Response.noContent().build(); // 204
        } catch (Exception ex) {
            return manejar(ex, "id");
        }
    }

    // ===================== Ayudas para los recursos =====================
    /**
     * GET de una lista hija, por ejemplo /orden_producto/orden/{id}. Busca el
     * padre (404 si no existe) y le pasa la consulta del DAO.
     */
    protected <Padre> Response listarPorPadre(String id, Class<Padre> tipoPadre, Function<Padre, List<?>> consulta) {
        UUID uuid = aUuid(id);
        if (uuid == null) {
            return error422("id", "El id no es un UUID valido.");
        }
        try {
            Padre padre = dao().buscarReferencia(tipoPadre, uuid);
            if (padre == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(consulta.apply(padre), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            return manejar(ex, "id");
        }
    }

    /**
     * Devuelve la entidad real de la base para una relacion que llego solo con
     * su id. Si viene null, devuelve null (los validarXxx del DAO se encargan
     * de decir que es obligatoria). Si el id no existe, responde 422.
     */
    protected <Relacion> Relacion referencia(Class<Relacion> claseRelacion, Relacion recibido, Function<Relacion, UUID> idDe, String campo) {
        if (recibido == null) {
            return null;
        }
        UUID id = idDe.apply(recibido);
        Relacion real = (id == null) ? null : dao().buscarReferencia(claseRelacion, id);
        if (real == null) {
            throw new DatoInvalidoException(campo, "No existe " + campo + " con id " + id + ".");
        }
        return real;
    }

    /**
     * El primer mensaje de error que no sea null, o null si todos pasaron.
     */
    protected static String primerError(String... errores) {
        for (String error : errores) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    protected Response error422(String campo, String mensaje) {
        return Response.status(422)
                .header("Wrong-Parameter", campo)
                .type("text/plain; charset=UTF-8")
                .entity(mensaje)
                .build();
    }

    /**
     * Excepcion para cortar con un 422 desde los ganchos.
     */
    protected static class DatoInvalidoException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private final String campo;

        public DatoInvalidoException(String campo, String mensaje) {
            super(mensaje);
            this.campo = campo;
        }

        public String getCampo() {
            return campo;
        }
    }

    // ===================== Interno =====================
    /**
     * Revisa las anotaciones de la entidad (@NotNull, @Size...).
     */
    private void validarAnotaciones(Entidad registro) {
        Set<ConstraintViolation<Entidad>> errores = validator.validate(registro);
        if (!errores.isEmpty()) {
            ConstraintViolation<Entidad> primero = errores.iterator().next();
            String campo = primero.getPropertyPath().toString();
            throw new DatoInvalidoException(campo, campo + ": " + primero.getMessage());
        }
    }

    /**
     * Traduce las excepciones a codigos HTTP. Se revisa toda la cadena de
     * causas porque el contenedor envuelve lo que lanza un EJB (@Stateless) en
     * un EJBException.
     */
    protected Response manejar(Exception ex, String campo) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            if (causa instanceof DatoInvalidoException) {
                return error422(((DatoInvalidoException) causa).getCampo(), causa.getMessage());
            }
            if (causa instanceof ConstraintViolationException) {
                return error422(campo, causa.getMessage());
            }
            if (causa instanceof IllegalArgumentException) {
                return error422(campo, causa.getMessage());
            }
            if (causa instanceof SQLException) {
                String estado = ((SQLException) causa).getSQLState();
                if (estado != null && estado.startsWith("23")) { // 23xxx = violacion de integridad
                    return error422(campo, "El registro esta en uso por otros datos o ya existe.");
                }
            }
        }
        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header("App-Error", "Error interno del servidor")
                .build();
    }

    private Entidad leer(String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            return null;
        }
        try {
            return JSONB.fromJson(cuerpo, claseEntidad);
        } catch (Exception ex) {
            return null;
        }
    }

    private static UUID aUuid(String texto) {
        try {
            return UUID.fromString(texto);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * "Rol" -> "rol", para el header Wrong-Parameter.
     */
    private String nombreRecurso() {
        String nombre = claseEntidad.getSimpleName();
        return Character.toLowerCase(nombre.charAt(0)) + nombre.substring(1);
    }
}
