package com.example.proyecto1.data;

import com.example.proyecto1.logic.PuestoCaracteristica;
import com.example.proyecto1.logic.PuestoCaracteristicaId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoCaracteristicaRepository extends CrudRepository<PuestoCaracteristica, PuestoCaracteristicaId> {
    List<PuestoCaracteristica> findByIdPuesto_Id(Integer idPuesto);

}


