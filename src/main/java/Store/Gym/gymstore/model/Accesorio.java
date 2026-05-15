package Store.Gym.gymstore.model;

import jakarta.persistence.*;
import lombok.*;

//aaa
@Data
@Entity
@Table(name = "accessories")
public class Accesorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Ej: "Cinturón de Powerlifting SBD"

    @Column(nullable = false)
    private String category; // Ej: "Equipamiento", "Suplementos"

    private Double price;
    private Integer stock;
}