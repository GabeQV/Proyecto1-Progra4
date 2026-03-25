package com.example.proyecto1.logic;

import com.example.proyecto1.data.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final UsuarioRepository usuarioRepo;
    private final PuestoRepository puestoRepository;
    private final PuestoCaracteristicaRepository puestoCaracteristicaRepository;
    private final OferenteRepository oferenteRepo;
    private final EmpresaRepository empresaRepo;
    private final PasswordEncoder passwordEncoder;
    private final CaracteristicaRepository caracteristicaRepository;
    private final OferenteHabilidadRepository oferenteHabilidadRepository;

    public Service(UsuarioRepository ur, OferenteRepository or, EmpresaRepository er,
                   PasswordEncoder pe, PuestoRepository po,
                   PuestoCaracteristicaRepository pcr,
                   CaracteristicaRepository cr,
                   OferenteHabilidadRepository ohr) {
        this.usuarioRepo = ur;
        this.oferenteRepo = or;
        this.empresaRepo = er;
        this.passwordEncoder = pe;
        this.puestoRepository = po;
        this.puestoCaracteristicaRepository = pcr;
        this.caracteristicaRepository = cr;
        this.oferenteHabilidadRepository = ohr;
    }

    // ----------------publico


    public List<Puesto> getTop5PuestosPublicos() {
        return puestoRepository.findTop5ByTipoPuestoAndActivoTrueOrderByFechaRegistroDesc("publico");
    }


    // ── USUARIO ──────────────────────────────────────────────────────────────

    public Optional<Usuario> buscarUsuarioPorId(String id) {
        return usuarioRepo.findById(id);
    }

    // ── OFERENTE ─────────────────────────────────────────────────────────────

    @Transactional
    public void registrarOferente(String id, String correo, String clave, String nombre,
                                  String primerApellido, String segundoApellido,
                                  String nacionalidad, String telefono, String residencia) {
        if (usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación.");
        }
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRolUsuario("OFERENTE");
        usuario.setActivo(false);

        Oferente oferente = new Oferente();
        oferente.setUsuario(usuario);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(primerApellido);
        oferente.setSegundoApellido(segundoApellido);
        oferente.setNacionalidad(nacionalidad);
        oferente.setTelefono(telefono);
        oferente.setResidencia(residencia);
        oferente.setAprobado(false);

        oferenteRepo.save(oferente);
    }

    @Transactional
    public void aprobarOferente(String id) {
        oferenteRepo.findById(id).ifPresent(oferente -> {
            oferente.setAprobado(true);
            Usuario usuario = oferente.getUsuario();
            usuario.setActivo(true);
            usuarioRepo.save(usuario);
        });
    }

    public Oferente buscarPorIdOf(String id) {
        return oferenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado."));
    }

    public List<Oferente> obtenerOferentesPendientes() {
        return oferenteRepo.findByAprobadoFalse();
    }

    @Transactional
    public void guardarCV(String idOferente, MultipartFile archivo, String uploadDir)
            throws java.io.IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se recibió ningún archivo.");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF.");
        }
        if (archivo.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo supera los 5 MB permitidos.");
        }
        Oferente oferente = oferenteRepo.findById(idOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado."));

        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        if (oferente.getCvRuta() != null) {
            Files.deleteIfExists(dirPath.resolve(oferente.getCvRuta()));
        }
        String nombreArchivo = idOferente + "_" + System.currentTimeMillis() + ".pdf";
        Path destino = dirPath.resolve(nombreArchivo);
        try (var inputStream = archivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
        }
        oferente.setCvRuta(nombreArchivo);
        oferenteRepo.save(oferente);
    }

    @Transactional
    public void eliminarCV(String idOferente, String uploadDir) throws java.io.IOException {
        Oferente oferente = oferenteRepo.findById(idOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado."));
        if (oferente.getCvRuta() != null) {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.deleteIfExists(dirPath.resolve(oferente.getCvRuta()));
            oferente.setCvRuta(null);
            oferenteRepo.save(oferente);
        }
    }

    // ── HABILIDADES OFERENTE ──────────────────────────────────────────────────

    public List<OferenteHabilidad> obtenerHabilidadesDeOferente(String idOferente) {
        return oferenteHabilidadRepository.findByIdOferente_Id(idOferente);
    }

    @Transactional
    public void agregarHabilidadOferente(String idOferente, Integer idCaracteristica, Integer nivel) {
        Oferente oferente = oferenteRepo.findById(idOferente)
                .orElseThrow(() -> new IllegalArgumentException("Oferente no encontrado."));
        Caracteristica caracteristica = caracteristicaRepository.findById(idCaracteristica)
                .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada."));

        OferenteHabilidadId compositeId = new OferenteHabilidadId();
        compositeId.setIdOferente(idOferente);
        compositeId.setIdCaracteristica(idCaracteristica);

        OferenteHabilidad habilidad = new OferenteHabilidad();
        habilidad.setId(compositeId);
        habilidad.setIdOferente(oferente);
        habilidad.setIdCaracteristica(caracteristica);
        habilidad.setNivel(nivel);

        oferenteHabilidadRepository.save(habilidad);
    }

    // ── EMPRESA ──────────────────────────────────────────────────────────────

    @Transactional
    public void registrarEmpresa(String id, String correo, String clave, String nombre,
                                 String localizacion, String telefono, String descripcion) {
        if (usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación.");
        }
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRolUsuario("EMPRESA");
        usuario.setActivo(false);

        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);
        empresa.setAprobado(false);
        empresaRepo.save(empresa);
    }

    @Transactional
    public void aprobarEmpresa(String id) {
        empresaRepo.findById(id).ifPresent(empresa -> {
            empresa.setAprobado(true);
            Usuario usuario = empresa.getUsuario();
            usuario.setActivo(true);
            usuarioRepo.save(usuario);
        });
    }

    public Empresa buscarPorIdEmp(String id) {
        return empresaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }

    public List<Empresa> obtenerEmpresasPendientes() {
        return empresaRepo.findByAprobadoFalse();
    }

    // ── PUESTOS ──────────────────────────────────────────────────────────────

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

    public List<Puesto> buscarPuestosPublicos(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return puestoRepository.findPuestosPublicosPorCaracteristicas(ids);
    }

    @Transactional
    public Puesto crearPuesto(String idEmpresa, String descripcion, Double salario, String tipoPuesto) {
        Empresa empresa = empresaRepo.findById(idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
        Puesto puesto = new Puesto();
        puesto.setIdEmpresa(empresa);
        puesto.setDescripcion(descripcion);
        puesto.setSalario(salario);
        puesto.setTipoPuesto(tipoPuesto);
        puesto.setActivo(true);
        puesto.setFechaRegistro(LocalDate.now());
        return puestoRepository.save(puesto);
    }

    /**
     * Asocia una característica con nivel requerido al puesto indicado.
     * Guarda en la tabla puesto_caracteristica (no en puesto).
     */
    @Transactional
    public void agregarCaracteristicaAPuesto(Integer idPuesto, Integer idCaracteristica, Integer nivel) {
        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado."));
        Caracteristica caracteristica = caracteristicaRepository.findById(idCaracteristica)
                .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada."));

        PuestoCaracteristicaId pk = new PuestoCaracteristicaId();
        pk.setIdPuesto(idPuesto);
        pk.setIdCaracteristica(idCaracteristica);

        PuestoCaracteristica pc = new PuestoCaracteristica();
        pc.setId(pk);
        pc.setIdPuesto(puesto);
        pc.setIdCaracteristica(caracteristica);
        pc.setNivelRequerido(nivel);

        puestoCaracteristicaRepository.save(pc);   // ← correcto: guarda PuestoCaracteristica
    }

    // ── CARACTERÍSTICAS ───────────────────────────────────────────────────────

    public List<Caracteristica> getCaracteristicasRaiz() {
        return caracteristicaRepository.findByIdPadreIsNull();
    }

    public List<Caracteristica> getSubCaracteristicas(Integer idPadre) {
        return caracteristicaRepository.findByIdPadre_Id(idPadre);
    }

    public Caracteristica getCaracteristica(Integer id) {
        return caracteristicaRepository.findById(id).orElse(null);
    }

    public List<Caracteristica> getBreadcrumbs(Integer idCategoria) {
        List<Caracteristica> breadcrumbs = new ArrayList<>();
        if (idCategoria == null) return breadcrumbs;
        Caracteristica actual = caracteristicaRepository.findById(idCategoria).orElse(null);
        while (actual != null) {
            breadcrumbs.add(actual);
            actual = actual.getIdPadre();
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }

    public List<Caracteristica> getAllCaracteristicas() {
        return caracteristicaRepository.findAll();
    }

    @Transactional
    public void addCaracteristica(Caracteristica nueva) {
        if (nueva.getIdPadre() != null) {
            Integer pid = nueva.getIdPadre().getId();
            if (pid == null || pid == 0) {
                nueva.setIdPadre(null);
            } else {
                Caracteristica padre = caracteristicaRepository.findById(pid).orElse(null);
                nueva.setIdPadre(padre);
            }
        }
        caracteristicaRepository.save(nueva);
    }
}