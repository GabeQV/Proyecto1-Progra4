package com.example.proyecto1.logic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "caracteristica")
public class Caracteristica {

    public Caracteristica(String nombre, Caracteristica idPadre) {
        this.nombre = nombre;
        this.idPadre = idPadre;
    }
    public Caracteristica(String nombre) {
        this.nombre = nombre;
    }
    public Caracteristica() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caracteristica", nullable = false)
    private Integer id;

    @Size(max = 60)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_padre")
    private Caracteristica idPadre;

    @OneToMany(mappedBy = "idPadre", fetch = FetchType.EAGER)
    private List<Caracteristica> hijos = new ArrayList<>();
}