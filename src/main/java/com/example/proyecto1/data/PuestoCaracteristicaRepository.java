package com.example.proyecto1.data;

import com.example.proyecto1.logic.PuestoCaracteristica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoCaracteristicaRepository extends CrudRepository<PuestoCaracteristica,String> {

}


