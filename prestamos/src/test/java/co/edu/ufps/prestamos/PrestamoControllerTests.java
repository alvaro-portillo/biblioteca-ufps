package co.edu.ufps.prestamos;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
class PrestamoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestamoRepository repositorio;

    @MockitoBean
    private LibrosClient librosClient;

    @BeforeEach
    void limpiarPrestamos() {
        repositorio.deleteAll();
    }

    @Test
    void creaPrestamoConDatosConsultadosYFechasPredeterminadas() throws Exception {
        LocalDate hoy = LocalDate.now();
        when(librosClient.obtenerLibro(1L))
                .thenReturn(new LibroResponse(1L, "Clean Code", 2));

        mockMvc.perform(post("/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libroId\":1,\"codigoEstudiante\":\"1152001\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.libroId").value(1))
                .andExpect(jsonPath("$.tituloLibro").value("Clean Code"))
                .andExpect(jsonPath("$.codigoEstudiante").value("1152001"))
                .andExpect(jsonPath("$.fechaPrestamo").value(hoy.toString()))
                .andExpect(jsonPath("$.fechaDevolucion").value(hoy.plusDays(15).toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));

        mockMvc.perform(get("/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoEstudiante").value("1152001"));
    }

    @Test
    void devuelve400CuandoLibrosNoEncuentraElLibro() throws Exception {
        when(librosClient.obtenerLibro(99L)).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "El libro 99 no existe"));

        mockMvc.perform(post("/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libroId\":99,\"codigoEstudiante\":\"1152001\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devuelve409CuandoNoHayEjemplares() throws Exception {
        when(librosClient.obtenerLibro(1L))
                .thenReturn(new LibroResponse(1L, "Clean Code", 0));

        mockMvc.perform(post("/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libroId\":1,\"codigoEstudiante\":\"1152001\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void devuelve503CuandoLibrosNoResponde() throws Exception {
        when(librosClient.obtenerLibro(1L)).thenThrow(
                new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de libros no responde"));

        mockMvc.perform(post("/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libroId\":1,\"codigoEstudiante\":\"1152001\"}"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void devuelve400YNoConsultaLibrosCuandoFaltaCodigoEstudiante() throws Exception {
        mockMvc.perform(post("/prestamos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"libroId\":1}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(librosClient);
    }
}
