package com.example.proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "puesto")
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa idEmpresa;

    @Size(max = 100)
    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "salario")
    private Double salario;

    @Size(max = 10)
    @Column(name = "tipo_puesto", length = 10)
    private String tipoPuesto;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;


}