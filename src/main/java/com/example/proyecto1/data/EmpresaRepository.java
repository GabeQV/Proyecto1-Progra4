package com.example.proyecto1.data;

import com.example.proyecto1.logic.Empresa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends CrudRepository<Empresa,String> {
    List<Empresa> findByAprobadoFalse();
}

