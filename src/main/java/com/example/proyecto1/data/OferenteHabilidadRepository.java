package com.example.proyecto1.data;

import com.example.proyecto1.logic.OferenteHabilidad;
import com.example.proyecto1.logic.OferenteHabilidadId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OferenteHabilidadRepository extends CrudRepository<OferenteHabilidad, OferenteHabilidadId> {

}


