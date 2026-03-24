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
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {


    private final UsuarioRepository usuarioRepo;
    private final PuestoRepository puestoRepository;
    private final OferenteRepository oferenteRepo;
    private final EmpresaRepository empresaRepo;
    private final PasswordEncoder passwordEncoder;
    private final CaracteristicaRepository caracteristicaRepository;


    public Service(UsuarioRepository ur, OferenteRepository or, EmpresaRepository er, PasswordEncoder pe, PuestoRepository po, CaracteristicaRepository cr) {
        this.usuarioRepo = ur;
        this.oferenteRepo = or;
        this.empresaRepo = er;
        this.passwordEncoder = pe;
        this.puestoRepository = po;
        this.caracteristicaRepository = cr;
    }


    @Transactional
    public void registrarOferente(String id, String correo, String clave, String nombre, String primerApellido, String segundoApellido, String nacionalidad, String telefono, String residencia) {

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
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
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

        // Convertir a ruta absoluta desde el directorio de trabajo real del proyecto
        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        // Borrar archivo físico anterior si existía
        if (oferente.getCvRuta() != null) {
            Files.deleteIfExists(dirPath.resolve(oferente.getCvRuta()));
        }

        // Nombre único: idOferente_timestamp.pdf
        String nombreArchivo = idOferente + "_" + System.currentTimeMillis() + ".pdf";
        Path destino = dirPath.resolve(nombreArchivo);

        // Files.copy con el InputStream evita el problema de transferTo en Tomcat
        try (var inputStream = archivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        oferente.setCvRuta(nombreArchivo);
        oferenteRepo.save(oferente);
    }

    /**
     * Elimina el CV del oferente: borra el archivo físico y pone cvRuta en null.
     */
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


    public List<Oferente> obtenerOferentesPendientes() {
        return oferenteRepo.findByAprobadoFalse();
    }

    public List<Empresa> obtenerEmpresasPendientes() {
        return empresaRepo.findByAprobadoFalse();
    }


    ///-----------------------------EMPRESA-----------------------------------------------------------------///

    @Transactional
    public void registrarEmpresa(String id, String correo, String clave, String nombre, String localizacion, String telefono, String descripcion) {
        if (usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación.");
        }
        // Crear usuario base — igual que en registrarOferente
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRolUsuario("EMPRESA");
        usuario.setActivo(false);  // espera aprobación del admin
        usuarioRepo.save(usuario);

        // Crear empresa vinculada — igual que creás el Oferente
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setNombre(nombre);
        empresa.setLocalizacion(localizacion);
        empresa.setTelefono(telefono);
        empresa.setDescripcion(descripcion);
        empresa.setAprobado(false);
        empresaRepo.save(empresa);
    }

    // ── Aprobar empresa
    @Transactional
    public void aprobarEmpresa(String id) {
        empresaRepo.findById(id).ifPresent(empresa -> {
            empresa.setAprobado(true);

            Usuario usuario = empresa.getUsuario();
            usuario.setActivo(true);  // ahora puede loguearse

            usuarioRepo.save(usuario);
        });
    }

    // ── Buscar empresa del usuario logueado ───────────────────
    // Lo usás en el dashboard para mostrar el nombre de la empresa
    public Empresa buscarPorIdEmp(String id) {
        return empresaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }


    ///-----------------------------PUESTOS-----------------------------------------------------------------///


    public List<Puesto> getPuestosDeEmpresa(String idEmpresa) {
        return puestoRepository.findByIdEmpresa_Id(idEmpresa);
    }

    @jakarta.transaction.Transactional
    public void desactivar(Integer id) {
        puestoRepository.findById(id).ifPresent(puesto -> {
            puesto.setActivo(false);
            puestoRepository.save(puesto);
        });
    }


    //     Crear puesto nuevo
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


    // Agregar característica con nivel al puesto recién creado

    public void agregarCaracteristica(Integer idPuesto, Integer idCaracteristica, Integer nivel) {

        Puesto puesto = puestoRepository.findById(idPuesto).orElse(null);
        if(puesto==null){
            throw new IllegalArgumentException("Puesto no encontrado.");
        }

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

        puestoRepository.save(pc.getIdPuesto());
    }
}
