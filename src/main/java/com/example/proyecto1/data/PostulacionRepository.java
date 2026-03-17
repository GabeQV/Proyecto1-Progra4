package com.example.proyecto1.data;

import com.example.proyecto1.logic.Postulacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostulacionRepository extends CrudRepository<Postulacion,Integer> {

}


