package com.kontagro.service.implementation;

import com.kontagro.dto.Class.AuthResponseDTO;
import com.kontagro.dto.Class.UsuarioDTO;
import com.kontagro.dto.Converter.UsuarioDTOConverter;
import com.kontagro.entities.Usuario;
import com.kontagro.exceptions.ResourceNotFoundException;
import com.kontagro.exceptions.UnauthorizedException;
import com.kontagro.repository.IUsuarioRepository;
import com.kontagro.security.AuthService;
import com.kontagro.service.contracts.IUsuarioService;
import com.kontagro.utils.MensajesError;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new UnauthorizedException(MensajesError.USUARIO_ERRADO));

        if (!passwordEncoder.matches(contrasena, usuarioEntity.getContrasena())) {
            throw new UnauthorizedException(MensajesError.USUARIO_ERRADO);
        }

        long expiresAt = System.currentTimeMillis() + authService.getAccessTokenExpirationMillis();
        String token = authService.generateToken(usuarioEntity, expiresAt);
        String refreshToken = authService.createRefreshToken(usuarioEntity);
        UsuarioDTO usuarioDTO = usuarioDTOConverter.convertToDTO(usuarioEntity);

        return new AuthResponseDTO(token, usuarioDTO, expiresAt, refreshToken);
    }



    @Override
    @Transactional
    public AuthResponseDTO refrescarSesion(String refreshToken) {
        Usuario usuario = authService.validateAndTouchRefreshToken(refreshToken);
        long expiresAt = System.currentTimeMillis() + authService.getAccessTokenExpirationMillis();
        return new AuthResponseDTO(
                authService.generateToken(usuario, expiresAt),
                usuarioDTOConverter.convertToDTO(usuario),
                expiresAt,
                refreshToken
        );
    }

    @Override
    public void cerrarSesion(String refreshToken) {
        authService.revokeRefreshToken(refreshToken);
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
