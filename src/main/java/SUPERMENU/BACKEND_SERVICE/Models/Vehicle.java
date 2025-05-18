package SUPERMENU.BACKEND_SERVICE.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
@Entity(name = "vehicle")
@Data
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String plate;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
