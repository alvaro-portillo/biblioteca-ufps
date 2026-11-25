package co.edu.ufps.libros;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listaLosTresLibrosIniciales() throws Exception {
        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void devuelve404CuandoNoExisteElLibro() throws Exception {
        mockMvc.perform(get("/libros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaUnLibroSinTitulo() throws Exception {
        mockMvc.perform(post("/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"autor\":\"X\",\"ejemplaresDisponibles\":1}"))
                .andExpect(status().isBadRequest());
    }
}
