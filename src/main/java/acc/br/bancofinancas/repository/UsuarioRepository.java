package acc.br.bancofinancas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByClienteIdAndRole(Integer clienteId, Role role);
    boolean existsByUsername(String username);
}
