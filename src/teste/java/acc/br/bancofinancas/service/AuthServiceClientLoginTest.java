package acc.br.bancofinancas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import acc.br.bancofinancas.dto.AuthRequest;
import acc.br.bancofinancas.dto.AuthResponse;
import acc.br.bancofinancas.model.Agencia;
import acc.br.bancofinancas.model.Cliente;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.AgenciaRepository;
import acc.br.bancofinancas.repository.ClienteRepository;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import acc.br.bancofinancas.security.JwtService;

class AuthServiceClientLoginTest {

    @Test
    void loginDeClienteComAgenciaNumeroContaESenhaQuatroDigitos() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        AgenciaRepository agenciaRepository = mock(AgenciaRepository.class);
        ContaCorrenteRepository contaCorrenteRepository = mock(ContaCorrenteRepository.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        JwtService jwtService = mock(JwtService.class);

        when(jwtService.generateToken(any())).thenReturn("jwt-cliente");

        AuthService authService = new AuthService(
                usuarioRepository,
                clienteRepository,
                agenciaRepository,
                contaCorrenteRepository,
                passwordEncoder,
                jwtService);

        Agencia agencia = new Agencia();
        agencia.setIdAgency(12);

        Cliente cliente = new Cliente();
        cliente.setIdCustomer(77);

        ContaCorrente conta = new ContaCorrente();
        conta.setAgencia(agencia);
        conta.setCliente(cliente);
        conta.setNumero(456);
        conta.setSenha(passwordEncoder.encode("1234"));

        when(contaCorrenteRepository.findByAgencia_IdAgencyAndNumero(12, 456))
                .thenReturn(Optional.of(conta));
        when(usuarioRepository.findByClienteIdAndRole(77, Role.CLIENTE))
                .thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthRequest request = new AuthRequest();
        request.setAgenciaId(12L);
        request.setNumeroConta(456);
        request.setPassword("1234");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-cliente", response.getToken());
        verify(usuarioRepository, org.mockito.Mockito.atLeastOnce()).save(any(Usuario.class));
    }
}
