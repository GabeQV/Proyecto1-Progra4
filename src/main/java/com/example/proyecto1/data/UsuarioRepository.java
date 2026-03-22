package com.example.proyecto1.data;

import com.example.proyecto1.logic.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,String> {
    Optional<Usuario> findById(String id);

}

