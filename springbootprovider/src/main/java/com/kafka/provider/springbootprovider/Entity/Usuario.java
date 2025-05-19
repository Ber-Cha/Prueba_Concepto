package com.kafka.provider.springbootprovider.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import org.antlr.v4.runtime.misc.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @EmbeddedId
    private UsuarioPK id;
    
    @Column(nullable = false)
    private String password;

    @Column(name = "apikey", nullable = false)
    private String apiKey;

    @OneToOne
    @MapsId("idpersona")
    @JoinColumn(name = "idpersona", foreignKey = @ForeignKey(name = "FK_usuario_persona"))
    @ToString.Exclude
    @JsonManagedReference
    private Persona persona;

}

