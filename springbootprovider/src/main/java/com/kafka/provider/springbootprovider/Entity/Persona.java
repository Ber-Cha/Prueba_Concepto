package com.kafka.provider.springbootprovider.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "Persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private int identificacion;
    @Column(nullable = false)
    private String pNombre;
    private String sNombre;
    @Column(nullable = false)
    private String pApellido;
    @Column(nullable = false)
    private String sApellido;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private LocalDate fechanacimiento;
    @Column(nullable = false)
    private int edad;
    @Column(nullable = false)
    private String edadclinica;
    @Column(nullable = false, name = "ubicacion")
    private String Ubicacion;

    @OneToOne(mappedBy = "persona", cascade = CascadeType.ALL)
    @JsonBackReference
    private Usuario usuario;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Coordenadas> coordenadas;

    @JsonIgnore
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<HistorialCoordenadas> historialCoordenadas;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Devolvemos una colección vacía o podemos asignar roles específicos
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        // Devolvemos la contraseña del usuario asociado
        return usuario != null ? usuario.getPassword() : null;
    }

    @Override
    public String getUsername() {
        // Devolvemos el login del usuario asociado
        return usuario != null ? usuario.getId().getLogin() : null;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Por defecto, la cuenta no expira
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Por defecto, la cuenta no está bloqueada
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Por defecto, las credenciales no expiran
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Por defecto, la cuenta está habilitada
        return true;
    }

}
