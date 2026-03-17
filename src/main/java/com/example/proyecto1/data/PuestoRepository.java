package com.example.proyecto1.data;

import com.example.proyecto1.logic.Puesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuestoRepository extends CrudRepository<PuestoRepository,String> {

}


