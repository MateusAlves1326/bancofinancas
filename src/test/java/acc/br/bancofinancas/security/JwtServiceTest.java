package acc.br.bancofinancas.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import acc.br.bancofinancas.model.Role;

class JwtServiceTest {

    @Test
    void deveGerarExtrairEValidarTokenParaUsuarioCorreto() {
        JwtService jwtService = new JwtService(
                "01234567890123456789012345678901", 3600);
        AuthenticatedUser user = new AuthenticatedUser("maria", "senha", Role.CLIENTE, 7, 2);

        String token = jwtService.generateToken(user);

        assertEquals("maria", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void deveInvalidarTokenParaUsuarioDiferente() {
        JwtService jwtService = new JwtService(
                "01234567890123456789012345678901", 3600);
        AuthenticatedUser user = new AuthenticatedUser("maria", "senha", Role.CLIENTE, 7, 2);
        AuthenticatedUser outroUsuario = new AuthenticatedUser("joao", "senha", Role.CLIENTE, 8, 2);

        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, outroUsuario));
    }
}
