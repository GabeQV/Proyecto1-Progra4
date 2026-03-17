package com.example.proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @Size(max = 20)
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Usuario usuario;

    @Size(max = 60)
    @Column(name = "nombre", length = 60)
    private String nombre;

    @Size(max = 60)
    @Column(name = "localizacion", length = 60)
    private String localizacion;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "aprobado")
    private Boolean aprobado;


}