package com.example.proyecto1.data;

import com.example.proyecto1.logic.Puesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto,Integer> {

    List<Puesto> findByIdEmpresa_Id(String idEmpresa);

    List<Puesto> findTop5ByTipoPuestoAndActivoTrueOrderByFechaRegistroDesc(String tipoPuesto);

}


