package co.edu.ufps.prestamos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LibrosClient {

    private final RestClient restClient;

    public LibrosClient(@Value("${libros.url}") String librosUrl) {
        this.restClient = RestClient.builder().baseUrl(librosUrl).build();
    }

    public LibroResponse obtenerLibro(Long libroId) {
        try {
            LibroResponse libro = restClient.get()
                    .uri("/libros/{id}", libroId)
                    .retrieve()
                    .body(LibroResponse.class);

            if (libro == null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE, "El servicio de libros devolvio una respuesta vacia");
            }

            return libro;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El libro " + libroId + " no existe", e);
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "El servicio de libros no responde", e);
        }
    }
}
