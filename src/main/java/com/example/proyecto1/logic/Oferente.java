package com.example.proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "oferente")
public class Oferente {
    @Id
    @Size(max = 20)
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id", nullable = false)
    private Usuario usuario;

    @Size(max = 40)
    @Column(name = "nombre", length = 40)
    private String nombre;

    @Size(max = 40)
    @Column(name = "primer_apellido", length = 40)
    private String primerApellido;

    @Size(max = 40)
    @Column(name = "segundo_apellido", length = 40)
    private String segundoApellido;

    @Size(max = 30)
    @Column(name = "nacionalidad", length = 30)
    private String nacionalidad;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 40)
    @Column(name = "residencia", length = 40)
    private String residencia;

    @Size(max = 255)
    @Column(name = "cvRuta")
    private String cvRuta;

    @Column(name = "aprobado")
    private Boolean aprobado;


}