package com.kafka.provider.springbootprovider.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Coordenadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordenadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coordenadas")
    private int id;
   
    @Column(name = "marca")
    private String marca;

    @Column(name = "longitud")
    private double longitud;

    @Column(name = "latitud")
    private double latitud;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false, foreignKey = @ForeignKey(name = "FK_Coordenadas_Persona"))
    @JsonBackReference
    private Persona persona;

}
