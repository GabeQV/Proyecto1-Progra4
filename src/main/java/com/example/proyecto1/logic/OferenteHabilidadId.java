package com.example.proyecto1.logic;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class OferenteHabilidadId implements Serializable {
    private static final long serialVersionUID = 3090520196577149919L;
    @Size(max = 20)
    @NotNull
    @Column(name = "id_oferente", nullable = false, length = 20)
    private String idOferente;

    @NotNull
    @Column(name = "id_caracteristica", nullable = false)
    private Integer idCaracteristica;


}