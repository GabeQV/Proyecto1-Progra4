package com.example.proyecto1.security;

import com.example.proyecto1.data.UsuarioRepository;
import com.example.proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Buscar por correo (login con correo)
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con correo: " + correo));

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRolUsuario()));

        boolean accountNonLocked = usuario.getActivo() != null && usuario.getActivo();

        // IMPORTANTE: el primer parámetro es el ID (cédula), no el correo.
        // Así principal.getName() en los controllers devuelve el id,
        // y findById() en guardarCV/dashboard sigue funcionando sin cambios.
        return new User(
                usuario.getId(),
                usuario.getClave(),
                true,
                true,
                true,
                accountNonLocked,
                authorities
        );
    }
}