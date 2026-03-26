package com.example.proyecto1.logic;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;

@Getter
@Immutable
@Entity
@Table(name = "vista_reporte_puestos")
public class ReportePuesto {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "empresa")
    private String empresa;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "salario")
    private Double salario;

    @Column(name = "tipo_puesto")
    private String tipoPuesto;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "moneda")
    private String moneda;
}