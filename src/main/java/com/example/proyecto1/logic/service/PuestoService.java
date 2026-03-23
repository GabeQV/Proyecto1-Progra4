package com.example.proyecto1.logic.service;

import com.example.proyecto1.logic.Puesto;
import com.example.proyecto1.data.PuestoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuestoService {

    @Autowired
    private PuestoRepository puestoRepository;

    public List<Puesto> getPuestosDeEmpresa(String idEmpresa) {
        return puestoRepository.findByIdEmpresa_Id(idEmpresa);
    }

    @Transactional
    public void desactivar(Integer id) {
        puestoRepository.findById(id).ifPresent(puesto -> {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        });
    }
}
