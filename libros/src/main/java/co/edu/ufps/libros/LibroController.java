package co.edu.ufps.libros;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroRepository repositorio;

    public LibroController(LibroRepository repositorio) {
        this.repositorio = repositorio;
    }

    // GET /libros
    @GetMapping
    public List<Libro> listar() {
        return repositorio.findAll();
    }

    // GET /libros/{id}
    @GetMapping("/{id}")
    public Libro obtener(@PathVariable("id") Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Libro " + id + " no encontrado"));
    }

    // POST /libros
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro crear(@Valid @RequestBody Libro libro) {
        libro.setId(null);
        return repositorio.save(libro);
    }

    // PUT /libros/{id}
    @PutMapping("/{id}")
    public Libro actualizar(@PathVariable("id") Long id, @Valid @RequestBody Libro datos) {
        Libro libro = obtener(id);
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setEjemplaresDisponibles(datos.getEjemplaresDisponibles());
        return repositorio.save(libro);
    }

    // DELETE /libros/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long id) {
        repositorio.delete(obtener(id));
    }
}