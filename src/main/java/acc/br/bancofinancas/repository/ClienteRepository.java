package acc.br.bancofinancas.repository;

import java.util.Optional;

import acc.br.bancofinancas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
	boolean existsByCpf(String cpf);
	Optional<Cliente> findByCpf(String cpf);
}
