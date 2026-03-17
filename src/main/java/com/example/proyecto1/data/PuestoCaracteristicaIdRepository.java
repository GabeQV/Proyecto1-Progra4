package com.example.proyecto1.data;

import com.example.proyecto1.logic.PuestoCaracteristicaId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoCaracteristicaIdRepository extends CrudRepository<PuestoCaracteristicaId,String> {

}



