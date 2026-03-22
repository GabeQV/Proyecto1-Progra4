package com.example.proyecto1.data;

import com.example.proyecto1.logic.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {

    List<Caracteristica> findByIdPadreIsNull();

    List<Caracteristica> findByIdPadre_Id(Integer idPadre);

}
