package acc.br.bancofinancas.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import acc.br.bancofinancas.dto.AuthRequest;
import acc.br.bancofinancas.dto.AuthResponse;
import acc.br.bancofinancas.dto.RegisterRequest;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;
import acc.br.bancofinancas.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            AgenciaRepository agenciaRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        validarVinculoPorRole(request);

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRole(request.getRole());
        usuario.setClienteId(request.getClienteId() == null ? null : request.getClienteId().intValue());
        usuario.setAgenciaId(request.getAgenciaId() == null ? null : request.getAgenciaId().intValue());

        usuarioRepository.save(usuario);

        AuthenticatedUser userDetails = new AuthenticatedUser(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRole(),
                usuario.getClienteId(),
                usuario.getAgenciaId());

        return new AuthResponse(jwtService.generateToken(userDetails));
    }

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        AuthenticatedUser principal = new AuthenticatedUser(
            usuario.getUsername(),
            usuario.getPassword(),
            usuario.getRole(),
            usuario.getClienteId(),
            usuario.getAgenciaId());

        return new AuthResponse(jwtService.generateToken(principal));
    }

    private void validarVinculoPorRole(RegisterRequest request) {
        if (request.getRole() == Role.CLIENTE) {
            if (request.getClienteId() == null) {
                throw new IllegalArgumentException("Usuário CLIENTE deve informar clienteId");
            }
            if (!clienteRepository.existsById(request.getClienteId().intValue())) {
                throw new IllegalArgumentException("Cliente informado não existe");
            }
        }

        if (request.getRole() == Role.AGENCIA) {
            if (request.getAgenciaId() == null) {
                throw new IllegalArgumentException("Usuário AGENCIA deve informar agenciaId");
            }
            if (!agenciaRepository.existsById(request.getAgenciaId().intValue())) {
                throw new IllegalArgumentException("Agência informada não existe");
            }
        }
    }
}
