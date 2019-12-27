package com.rolandopalermo.facturacion.ec.service.impl;

import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import com.rolandopalermo.facturacion.ec.dto.PasswordDTO;
import com.rolandopalermo.facturacion.ec.dto.UsuarioDTO;
import com.rolandopalermo.facturacion.ec.persistence.entity.User;
import com.rolandopalermo.facturacion.ec.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userServiceImpl")
public class UserServiceImpl extends GenericCRUDServiceImpl<User, UsuarioDTO> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository domainRepository;

    public void updatePassword(String username, PasswordDTO passwordDTO) {
        if (passwordDTO.getOldPassword().compareTo(passwordDTO.getNewPassword()) == 0) {
            throw new VeronicaException("La nueva contraseña no puede ser igual a la anterior");
        }
        String newEncodedPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUsername(username);
        Optional<User> optionalUser = findExisting(usuarioDTO);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException(String.format("El usuario %s no se encuentra registrado", username));
        }
        User user = optionalUser.get();
        String storedPassword = user.getPassword();
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), storedPassword)) {
            throw new VeronicaException(
                    String.format("No se pudo actualizar la contraseña del usuario %s", username));
        }
        user.setPassword(newEncodedPassword);
        domainRepository.save(user);
    }

    @Override
    public User mapTo(UsuarioDTO usuarioDTO) {
        User user = new User();
        user.setActive(usuarioDTO.isActive());
        user.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        user.setRole(usuarioDTO.getRol());
        user.setUsername(usuarioDTO.getUsername());
        return user;
    }

    @Override
    public Optional<User> findExisting(UsuarioDTO domainObject) {
        return domainRepository.findByUsername(domainObject.getUsername());
    }

    @Override
    public UsuarioDTO build(User domainObject) {
        return null;
    }

}