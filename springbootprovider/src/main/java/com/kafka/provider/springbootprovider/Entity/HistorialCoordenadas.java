package com.kafka.provider.springbootprovider.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historial_coordenadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCoordenadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private int id;

    @Column(name = "latitud")
    private double latitud;

    @Column(name = "longitud")
    private double longitud;

    @Column(name = "marca")
    private String marca;

    @Column(nullable = false, name = "ubicacion")
    private String ubicacion;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Historial_Persona"))
    @JsonBackReference
    private Persona persona;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

}

