package com.example.proyecto1.data;

import com.example.proyecto1.logic.Oferente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OferenteRepository extends CrudRepository<Oferente,String> {
    List<Oferente> findByAprobadoFalse();
}

