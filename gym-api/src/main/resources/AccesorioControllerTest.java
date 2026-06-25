package Store.Gym.gymstore.controller;

import Store.Gym.gymstore.model.Accesorio;
import Store.Gym.gymstore.repository.AccesorioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// IMPORTACIÓN NUEVA PARA VERSIONES RECIENTES DE SPRING BOOT
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

// IMPORTACIONES DE MOCKITO
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccesorioController.class)
class AccesorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // AQUI ESTA LA MAGIA: Usamos @MockitoBean en lugar de @MockBean
    @MockitoBean
    private AccesorioRepository accesorioRepository;

    @Test
    void findAll_ShouldReturnAccessoriesList() throws Exception {
        Accesorio accesorio = new Accesorio();
        accesorio.setId(1L);
        accesorio.setName("Cinturon de Cuero");
        accesorio.setCategory("Equipamiento");
        accesorio.setPrice(25000.0);
        accesorio.setStock(10);

        when(accesorioRepository.findAll()).thenReturn(Arrays.asList(accesorio));

        mockMvc.perform(get("/api/accessorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cinturon de Cuero"))
                .andExpect(jsonPath("$[0].category").value("Equipamiento"));
    }

    @Test
    void save_ShouldReturnSavedAccessory() throws Exception {
        Accesorio accesorio = new Accesorio();
        accesorio.setId(1L);
        accesorio.setName("Mancuernas 5kg");
        accesorio.setCategory("Pesas");
        accesorio.setPrice(15000.0);
        accesorio.setStock(20);

        when(accesorioRepository.save(any(Accesorio.class))).thenReturn(accesorio);

        String jsonContent = "{\"name\":\"Mancuernas 5kg\", \"category\":\"Pesas\", \"price\":15000.0, \"stock\":20}";

        mockMvc.perform(post("/api/accessorios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mancuernas 5kg"));
    }
}