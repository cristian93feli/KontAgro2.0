package com.kontagro.service.implementation;

import com.kontagro.dto.Class.AuthResponseDTO;
import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.dto.Converter.UsuarioDTOConverter;
import com.kontagro.entities.Usuario;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.repository.IUsuarioRepository;
import com.kontagro.security.AuthService;
import com.kontagro.service.contracts.IUsuarioService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository iUsuarioRepository;
    private final UsuarioDTOConverter usuarioDTOConverter;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioDTOConverter.convertToEntity(usuarioDTO);

        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));

        return usuarioDTOConverter.convertToDTO(iUsuarioRepository.save(usuario));
    }

    public AuthResponseDTO login(String usuario, String contrasena) {
        Usuario usuarioEntity = iUsuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException(MensajesError.USUARIO_ERRADO));

        if (!passwordEncoder.matches(contrasena, usuarioEntity.getContrasena())) {
            throw new ResourceNotFoundException(MensajesError.USUARIO_ERRADO);
        }

        String token = authService.generateToken(usuarioEntity);
        UsuarioDTO usuarioDTO = usuarioDTOConverter.convertToDTO(usuarioEntity);

        return new AuthResponseDTO(token, usuarioDTO);
    }


    @Override
    public UsuarioDTO consultarUsuario(Long id) {

        Optional<Usuario> usuarioOptional = iUsuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            return usuarioDTOConverter.convertToDTO(usuarioOptional.get());
        }
        throw new ResourceNotFoundException(
                String.format(MensajesError.USUARIO_NO_ENCONTRADO, id));
    }

    @Override
    public UsuarioDTO actualizarUsuario(UsuarioDTO usuarioDTO) {

        consultarUsuario(usuarioDTO.getId());
        return usuarioDTOConverter.convertToDTO
                (iUsuarioRepository.save(usuarioDTOConverter.convertToEntity(usuarioDTO)));
    }


    @Override
    public void eliminarUsuario(Long id) {

        if (!iUsuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(MensajesError.USUARIO_NO_ENCONTRADO, id));
        }
        iUsuarioRepository.deleteById(id);
    }
}
