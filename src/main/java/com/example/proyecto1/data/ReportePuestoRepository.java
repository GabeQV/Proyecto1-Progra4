package com.example.proyecto1.data;

import com.example.proyecto1.logic.ReportePuesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportePuestoRepository
        extends CrudRepository<ReportePuesto, Integer> {

    List<ReportePuesto> findByMesAndAnio(int mes, int anio);
}