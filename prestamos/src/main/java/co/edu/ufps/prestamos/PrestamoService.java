package co.edu.ufps.prestamos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrestamoService {

    private final PrestamoRepository repositorio;
    private final LibrosClient librosClient;

    public PrestamoService(PrestamoRepository repositorio, LibrosClient librosClient) {
        this.repositorio = repositorio;
        this.librosClient = librosClient;
    }

    public List<Prestamo> listar() {
        return repositorio.findAll();
    }

    public Prestamo crear(PrestamoRequest request) {
        LibroResponse libro = librosClient.obtenerLibro(request.libroId());
        Integer ejemplaresDisponibles = libro.ejemplaresDisponibles();

        if (ejemplaresDisponibles == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "El servicio de libros devolvio datos incompletos");
        }
        if (ejemplaresDisponibles <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay ejemplares disponibles");
        }

        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setLibroId(request.libroId());
        prestamo.setTituloLibro(libro.titulo());
        prestamo.setCodigoEstudiante(request.codigoEstudiante());
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaDevolucion(hoy.plusDays(15));
        prestamo.setEstado("ACTIVO");

        return repositorio.save(prestamo);
    }
}
