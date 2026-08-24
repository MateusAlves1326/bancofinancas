package acc.br.bancofinancas.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import acc.br.bancofinancas.dto.AuthRequest;
import acc.br.bancofinancas.dto.AuthResponse;
import acc.br.bancofinancas.dto.RegisterRequest;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import acc.br.bancofinancas.security.AuthenticatedUser;
import acc.br.bancofinancas.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            AgenciaRepository agenciaRepository,
            ContaCorrenteRepository contaCorrenteRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
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
        if ((request.getUsername() == null || request.getUsername().isBlank())
                && request.getAgenciaId() != null
                && request.getNumeroConta() != null
                && request.getPassword() != null) {
            return loginClientePorConta(request);
        }

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

    private AuthResponse loginClientePorConta(AuthRequest request) {
        ContaCorrente conta = contaCorrenteRepository.findByAgencia_IdAgencyAndNumero(
                request.getAgenciaId().intValue(), request.getNumeroConta())
                .orElseThrow(() -> new IllegalArgumentException("Dados do cliente inválidos"));

        String senha = request.getPassword();
        if (senha == null || !senha.matches("\\d{4}")) {
            throw new IllegalArgumentException("A senha deve conter exatamente 4 dígitos");
        }

        String senhaDaConta = conta.getSenha();
        boolean senhaValida = passwordEncoder.matches(senha, senhaDaConta)
                || Objects.equals(senha, senhaDaConta);

        if (!senhaValida) {
            throw new IllegalArgumentException("Dados do cliente inválidos");
        }

        Cliente cliente = conta.getCliente();
        Usuario usuario = usuarioRepository.findByClienteIdAndRole(cliente.getIdCustomer(), Role.CLIENTE)
                .orElseGet(() -> {
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.setUsername("cliente-" + cliente.getIdCustomer());
                    novoUsuario.setPassword(passwordEncoder.encode(senha));
                    novoUsuario.setRole(Role.CLIENTE);
                    novoUsuario.setClienteId(cliente.getIdCustomer());
                    novoUsuario.setAgenciaId(conta.getAgencia().getIdAgency());
                    return usuarioRepository.save(novoUsuario);
                });

        usuario.setPassword(passwordEncoder.encode(senha));
        usuario.setAgenciaId(conta.getAgencia().getIdAgency());
        usuarioRepository.save(usuario);

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
