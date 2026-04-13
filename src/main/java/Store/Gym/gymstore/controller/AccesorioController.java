package Store.Gym.gymstore.controller;

import Store.Gym.gymstore.model.Accesorio;
import Store.Gym.gymstore.repository.AccesorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessorios")
public class AccesorioController {

    @Autowired
    private AccesorioRepository accesorioRepository;

    @GetMapping
    public List<Accesorio> findAll() {
        return accesorioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Accesorio> save(@RequestBody Accesorio accesorio) {
        Accesorio saved = accesorioRepository.save(accesorio);
        return ResponseEntity.ok().body(saved);
    }

}
