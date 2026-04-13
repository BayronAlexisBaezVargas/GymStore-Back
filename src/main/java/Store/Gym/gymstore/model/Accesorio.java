package Store.Gym.gymstore.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name= "accesorios")
public class Accesorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;
}
