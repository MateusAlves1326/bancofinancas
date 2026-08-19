package acc.br.bancofinancas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
