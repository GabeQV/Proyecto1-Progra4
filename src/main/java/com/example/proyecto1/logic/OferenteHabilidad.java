package com.example.proyecto1.logic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "oferente_habilidad")
public class OferenteHabilidad {
    @EmbeddedId
    private OferenteHabilidadId id;

    @MapsId("idOferente")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_oferente", nullable = false)
    private Oferente idOferente;

    @MapsId("idCaracteristica")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caracteristica", nullable = false)
    private Caracteristica idCaracteristica;

    @Column(name = "nivel")
    private Integer nivel;


}