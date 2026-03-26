package com.example.proyecto1.data;

import com.example.proyecto1.logic.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {

    List<Caracteristica> findByIdPadreIsNull();

    List<Caracteristica> findByIdPadre_Id(Integer idPadre);

    @Query("SELECT c FROM Caracteristica c WHERE c.id = :id OR c.idPadre.id = :id")
    List<Caracteristica> findByIdOrIdPadre(@Param("id") Integer id);

}
