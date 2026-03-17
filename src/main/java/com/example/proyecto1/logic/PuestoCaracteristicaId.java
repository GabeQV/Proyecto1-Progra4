package com.example.proyecto1.logic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class PuestoCaracteristicaId implements Serializable {
    private static final long serialVersionUID = 2445500731827768825L;
    @NotNull
    @Column(name = "id_puesto", nullable = false)
    private Integer idPuesto;

    @NotNull
    @Column(name = "id_caracteristica", nullable = false)
    private Integer idCaracteristica;


}