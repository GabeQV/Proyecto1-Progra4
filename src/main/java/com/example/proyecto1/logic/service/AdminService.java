package com.example.proyecto1.logic.service;

import com.example.proyecto1.data.CaracteristicaRepository;
import com.example.proyecto1.data.EmpresaRepository;
import com.example.proyecto1.data.OferenteRepository;
import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Caracteristica;
import com.example.proyecto1.logic.Empresa;
import com.example.proyecto1.logic.Oferente;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final OferenteRepository oferenteRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    @Autowired
    public AdminService(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository,
                        OferenteRepository oferenteRepository,  CaracteristicaRepository caracteristicaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.oferenteRepository = oferenteRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    // --- METODOS DE APROBACION ---

    public List<Empresa> getEmpresasPendientes() {
        return empresaRepository.findByAprobadoFalse();
    }

    @Transactional
    public void aprobarEmpresa(String empresaId) {
        empresaRepository.findById(empresaId).ifPresent(empresa -> {
            empresa.setAprobado(true);
            Usuario usuario = empresa.getUsuario();
            if (usuario != null) {
                usuario.setActivo(true);
                usuarioRepository.save(usuario);
            }
        });
    }

    public List<Oferente> getOferentesPendientes() {
        return oferenteRepository.findByAprobadoFalse();
    }

    @Transactional
    public void aprobarOferente(String oferenteId) {
        oferenteRepository.findById(oferenteId).ifPresent(oferente -> {
            oferente.setAprobado(true);
            Usuario usuario = oferente.getUsuario();
            if (usuario != null) {
                usuario.setActivo(true);
                usuarioRepository.save(usuario);
            }
        });
    }

    // --- METODOS DE CARACTERISTICAS ---

    public Map<String, Object> getCaracteristicasViewData(Integer actualId) {
        Map<String, Object> viewData = new HashMap<>();
        List<Caracteristica> subCategorias;
        List<Caracteristica> breadcrumbs = new ArrayList<>();

        if (actualId == null) {
            subCategorias = caracteristicaRepository.findByIdPadreIsNull();
            viewData.put("titulo", "Categorías: raíces");
        } else {
            subCategorias = caracteristicaRepository.findByIdPadre_Id(actualId);
            Caracteristica actual = caracteristicaRepository.findById(actualId).orElse(null);
            viewData.put("titulo", "Subcategorías de: " + (actual != null ? actual.getNombre() : ""));
            while (actual != null) {
                breadcrumbs.add(actual);
                actual = actual.getIdPadre();
            }
            Collections.reverse(breadcrumbs);
        }

        viewData.put("subCategorias", subCategorias);
        viewData.put("breadcrumbs", breadcrumbs);
        viewData.put("listaTodosPadres", caracteristicaRepository.findAll());

        return viewData;
    }

    @Transactional
    public void addCaracteristica(Caracteristica nuevaCaracteristica) {
        // La logica de si el padre es nulo o no se maneja en el controlador antes de llamar a este metodo.
        caracteristicaRepository.save(nuevaCaracteristica);
    }

}
