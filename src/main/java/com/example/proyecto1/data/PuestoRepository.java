package com.example.proyecto1.data;

import com.example.proyecto1.logic.Puesto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto,Integer> {

    List<Puesto> findByIdEmpresa_Id(String idEmpresa);

    List<Puesto> findTop5ByTipoPuestoAndActivoTrueOrderByFechaRegistroDesc(String tipoPuesto);
    @Query("SELECT DISTINCT p FROM Puesto p " +
            "LEFT JOIN FETCH p.caracteristicas pc " +
            "LEFT JOIN pc.idCaracteristica " +
            "JOIN p.caracteristicas req_pc " +
            "WHERE p.tipoPuesto = 'publico' " +
            "AND p.activo = true " +
            "AND req_pc.idCaracteristica.id IN :ids")
    List<Puesto> findPuestosPublicosPorCaracteristicas(@Param("ids") List<Integer> ids);

    @Query("SELECT DISTINCT p FROM Puesto p " +
            "LEFT JOIN FETCH p.caracteristicas pc " +
            "LEFT JOIN pc.idCaracteristica " +
            "JOIN p.caracteristicas req_pc " +
            "WHERE p.activo = true " +
            "AND req_pc.idCaracteristica.id IN :ids")
    List<Puesto> findPuestosPorCaracteristicas(@Param("ids") List<Integer> ids);



}


