package com.example.proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @Size(max = 20)
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Size(max = 40)
    @Column(name = "correo", length = 40)
    private String correo;

    @Size(max = 255)
    @Column(name = "clave")
    private String clave;

    @Size(max = 20)
    @Column(name = "rolUsuario", length = 20)
    private String rolUsuario;

    @Column(name = "activo")
    private Boolean activo;

    // Relación de vuelta hacia Empresa
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Empresa empresa;

    // Relación de vuelta hacia Oferente
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Oferente oferente;

}