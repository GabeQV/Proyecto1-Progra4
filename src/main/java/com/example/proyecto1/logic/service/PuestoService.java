package com.example.proyecto1.logic.service;

import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Puesto;
import com.example.proyecto1.data.PuestoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PuestoService {

    @Autowired
    private PuestoRepository puestoRepository;
    private EmpresaRepository empresaRepository;

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

    // Crear puesto nuevo
    @Transactional
    public Puesto crearPuesto(String idEmpresa, String descripcion,
                              Double salario, String tipoPuesto) {

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));

        Puesto puesto = new Puesto();
        puesto.setIdEmpresa(empresa);
        puesto.setDescripcion(descripcion);
        puesto.setSalario(salario);
        puesto.setTipoPuesto(tipoPuesto);
        puesto.setActivo(true);
        puesto.setFechaRegistro(LocalDate.now());

        return puestoRepository.save(puesto);
        // retorna el puesto con su ID generado
        // lo necesitás para luego agregarle características
    }

//    // Agregar característica con nivel al puesto recién creado
//    @Transactional
//    public void agregarCaracteristica(Integer idPuesto,
//                                      Integer idCaracteristica,
//                                      Integer nivel) {
//
//        Puesto puesto = puestoRepository.findById(idPuesto)
//                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado."));
//
//        Caracteristica caracteristica = caracteristicaRepo.findById(idCaracteristica)
//                .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada."));
//
//        PuestoCaracteristicaId pk = new PuestoCaracteristicaId();
//        pk.setIdPuesto(idPuesto);
//        pk.setIdCaracteristica(idCaracteristica);
//
//        PuestoCaracteristica pc = new PuestoCaracteristica();
//        pc.setId(pk);
//        pc.setIdPuesto(puesto);
//        pc.setIdCaracteristica(caracteristica);
//        pc.setNivelRequerido(nivel);
//
//        pcRepo.save(pc);
//    }
}
