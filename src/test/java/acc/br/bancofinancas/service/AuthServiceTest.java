package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import acc.br.bancofinancas.dto.AuthRequest;
import acc.br.bancofinancas.dto.AuthResponse;
import acc.br.bancofinancas.dto.RegisterRequest;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import acc.br.bancofinancas.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private ContaCorrenteRepository contaCorrenteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRegistrarUsuarioDeAgenciaComSenhaCodificada() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("agencia1");
        request.setPassword("1234");
        request.setRole(Role.AGENCIA);
        request.setAgenciaId(3L);

        when(agenciaRepository.existsById(3)).thenReturn(true);
        when(passwordEncoder.encode("1234")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals("token", response.getToken());
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("agencia1", captor.getValue().getUsername());
        assertEquals("hash", captor.getValue().getPassword());
        assertEquals(Role.AGENCIA, captor.getValue().getRole());
        assertEquals(3, captor.getValue().getAgenciaId());
    }

    @Test
    void deveRegistrarUsuarioDeClienteQuandoClienteExiste() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("cliente1");
        request.setPassword("senha");
        request.setRole(Role.CLIENTE);
        request.setClienteId(7L);

        when(clienteRepository.existsById(7)).thenReturn(true);
        when(passwordEncoder.encode("senha")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        assertEquals("token", authService.register(request).getToken());
        verify(clienteRepository).existsById(7);
    }

    @Test
    void deveRecusarUsernameJaCadastrado() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        when(usuarioRepository.existsByUsername("admin")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Username já está em uso", exception.getMessage());
    }

    @Test
    void deveRecusarClienteSemVinculo() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("cliente1");
        request.setRole(Role.CLIENTE);

        when(usuarioRepository.existsByUsername("cliente1")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Usuário CLIENTE deve informar clienteId", exception.getMessage());
    }

    @Test
    void deveRecusarAgenciaInexistente() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("agencia1");
        request.setRole(Role.AGENCIA);
        request.setAgenciaId(3L);

        when(agenciaRepository.existsById(3)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Agência informada não existe", exception.getMessage());
    }

    @Test
    void deveRecusarClienteInexistente() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("cliente1");
        request.setRole(Role.CLIENTE);
        request.setClienteId(7L);
        when(clienteRepository.existsById(7)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Cliente informado não existe", exception.getMessage());
    }

    @Test
    void deveRecusarAgenciaSemVinculo() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("agencia1");
        request.setRole(Role.AGENCIA);
        when(usuarioRepository.existsByUsername("agencia1")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Usuário AGENCIA deve informar agenciaId", exception.getMessage());
    }

    @Test
    void deveRecusarLoginPorContaComSenhaForaDoFormato() {
        AuthRequest request = new AuthRequest();
        request.setAgenciaId(1L);
        request.setNumeroConta(2);
        request.setPassword("123");
        when(contaCorrenteRepository.findByAgencia_IdAgencyAndNumero(1, 2))
                .thenReturn(Optional.of(new acc.br.bancofinancas.model.ContaCorrente()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("A senha deve conter exatamente 4 dígitos", exception.getMessage());
    }

    @Test
    void deveFazerLoginComUsuarioESenhaValidos() {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("senha");

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("hash");
        usuario.setRole(Role.ADMIN);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha", "hash")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("token");

        assertEquals("token", authService.login(request).getToken());
    }

    @Test
    void deveRecusarLoginQuandoUsuarioNaoExiste() {
        AuthRequest request = new AuthRequest();
        request.setUsername("inexistente");
        request.setPassword("senha");
        when(usuarioRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    void deveRecusarLoginQuandoSenhaNaoConfere() {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("errada");

        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("hash");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }
}
