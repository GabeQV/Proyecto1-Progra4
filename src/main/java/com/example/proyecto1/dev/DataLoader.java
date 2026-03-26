package com.example.proyecto1.dev;

import com.example.proyecto1.data.*;
import com.example.proyecto1.logic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CaracteristicaRepository caracteristicaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PuestoRepository puestoRepository;
    @Autowired private OferenteHabilidadRepository oferenteHabilidadRepository;
    @Autowired private PuestoCaracteristicaRepository puestoCaracteristicaRepository;
    @Autowired private EmpresaRepository empresaRepository; // Para buscar empresa
    @Autowired private OferenteRepository oferenteRepository; // Para buscar oferente

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            System.out.println("La base de datos ya contiene datos. No se cargará nada.");
            return;
        }

        System.out.println("Cargando datos de prueba...");
        String password = passwordEncoder.encode("123");

        // 1. CREAR CARACTERÍSTICAS JERÁRQUICAS
        System.out.println("Creando características...");
        Caracteristica lprog = createCaracteristica("Lenguajes de Programación", null);
        Caracteristica java = createCaracteristica("Java", lprog);
        Caracteristica python = createCaracteristica("Python", lprog);
        Caracteristica js = createCaracteristica("JavaScript", lprog);

        Caracteristica bd = createCaracteristica("Bases de Datos", null);
        Caracteristica rel = createCaracteristica("Relacionales", bd);
        Caracteristica mysql = createCaracteristica("MySQL", rel);
        Caracteristica nosql = createCaracteristica("NoSQL", bd);
        Caracteristica mongo = createCaracteristica("MongoDB", nosql);

        Caracteristica fw = createCaracteristica("Frameworks y Librerías", null);
        Caracteristica front = createCaracteristica("Frontend", fw);
        Caracteristica react = createCaracteristica("React", front);
        Caracteristica backend = createCaracteristica("Backend", fw);
        Caracteristica spring = createCaracteristica("Spring Boot", backend);

        // 2. CREAR USUARIOS Y ROLES
        System.out.println("Creando usuarios...");

        // Admin
        createAdmin("admin", "admin@bolsa.com", password);

        // Empresas
        Empresa empresaAprobada = createEmpresa("empresa1", "empresa1@test.com", password, "Tech Solutions Inc.", true);
        Empresa empresaPendiente = createEmpresa("empresa2", "empresa2@test.com", password, "Innovatec", false);

        // Oferentes
        Oferente oferenteAprobado1 = createOferente("oferente1", "oferente1@test.com", password, "Ana", "Gomez", true);
        Oferente oferenteAprobado2 = createOferente("oferente3", "oferente3@test.com", password, "Luis", "Mora", true);
        Oferente oferentePendiente = createOferente("oferente2", "oferente2@test.com", password, "Carlos", "Rojas", false);


        // 3. ASIGNAR HABILIDADES A OFERENTES
        System.out.println("Asignando habilidades...");
        addHabilidad(oferenteAprobado1, java, 4);
        addHabilidad(oferenteAprobado1, spring, 3);
        addHabilidad(oferenteAprobado1, mysql, 3);

        addHabilidad(oferenteAprobado2, python, 4);
        addHabilidad(oferenteAprobado2, mongo, 2);
        addHabilidad(oferenteAprobado2, react, 3);


        // 4. CREAR PUESTOS PARA EMPRESAS
        System.out.println("Creando puestos...");
        Puesto p1 = createPuesto(empresaAprobada, "Desarrollador Java Backend", 2500.0, "publico", "$");
        addRequisitoPuesto(p1, java, 3);
        addRequisitoPuesto(p1, spring, 2);
        addRequisitoPuesto(p1, mysql, 2);

        Puesto p2 = createPuesto(empresaAprobada, "Desarrollador Fullstack Python", 2200.0, "publico", "$");
        addRequisitoPuesto(p2, python, 3);
        addRequisitoPuesto(p2, react, 2);

        Puesto p3 = createPuesto(empresaAprobada, "Desarrollador Frontend", 1800.0, "privado", "$");
        addRequisitoPuesto(p3, js, 3);
        addRequisitoPuesto(p3, react, 3);

        System.out.println("Datos de prueba cargados exitosamente.");
    }

    // --- Métodos de ayuda para crear entidades ---

    private Caracteristica createCaracteristica(String nombre, Caracteristica padre) {
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        c.setIdPadre(padre);
        return caracteristicaRepository.save(c);
    }

    private void createAdmin(String id, String correo, String clave) {
        Usuario admin = new Usuario();
        admin.setId(id);
        admin.setCorreo(correo);
        admin.setClave(clave);
        admin.setRolUsuario("ADMIN");
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }

    private Empresa createEmpresa(String id, String correo, String clave, String nombre, boolean aprobado) {
        Usuario user = new Usuario();
        user.setId(id);
        user.setCorreo(correo);
        user.setClave(clave);
        user.setRolUsuario("EMPRESA");
        user.setActivo(aprobado);

        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNombre(nombre);
        empresa.setAprobado(aprobado);
        empresa.setUsuario(user); // Vinculación clave

        user.setEmpresa(empresa);
        usuarioRepository.save(user);
        return empresaRepository.findById(id).orElse(null);
    }

    private Oferente createOferente(String id, String correo, String clave, String nombre, String apellido, boolean aprobado) {
        Usuario user = new Usuario();
        user.setId(id);
        user.setCorreo(correo);
        user.setClave(clave);
        user.setRolUsuario("OFERENTE");
        user.setActivo(aprobado);

        Oferente oferente = new Oferente();
        oferente.setId(id);
        oferente.setNombre(nombre);
        oferente.setPrimerApellido(apellido);
        oferente.setAprobado(aprobado);
        oferente.setUsuario(user); // Vinculación clave

        user.setOferente(oferente);
        usuarioRepository.save(user);
        return oferenteRepository.findById(id).orElse(null);
    }

    private Puesto createPuesto(Empresa empresa, String desc, double salario, String tipo, String moneda) {
        Puesto p = new Puesto();
        p.setIdEmpresa(empresa);
        p.setDescripcion(desc);
        p.setSalario(salario);
        p.setTipoPuesto(tipo);
        p.setMoneda(moneda);
        p.setActivo(true);
        p.setFechaRegistro(LocalDate.now());
        return puestoRepository.save(p);
    }

    private void addHabilidad(Oferente oferente, Caracteristica car, int nivel) {
        OferenteHabilidadId id = new OferenteHabilidadId();
        id.setIdOferente(oferente.getId());
        id.setIdCaracteristica(car.getId());

        OferenteHabilidad hab = new OferenteHabilidad();
        hab.setId(id);
        hab.setIdOferente(oferente);
        hab.setIdCaracteristica(car);
        hab.setNivel(nivel);
        oferenteHabilidadRepository.save(hab);
    }

    private void addRequisitoPuesto(Puesto puesto, Caracteristica car, int nivel) {
        PuestoCaracteristicaId id = new PuestoCaracteristicaId();
        id.setIdPuesto(puesto.getId());
        id.setIdCaracteristica(car.getId());

        PuestoCaracteristica req = new PuestoCaracteristica();
        req.setId(id);
        req.setIdPuesto(puesto);
        req.setIdCaracteristica(car);
        req.setNivelRequerido(nivel);
        puestoCaracteristicaRepository.save(req);
    }
}