package acc.br.bancofinancas.repository;

import java.util.Optional;

import acc.br.bancofinancas.model.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {

	boolean existsByCliente_IdCustomerAndAgencia_IdAgency(int clienteId, int agenciaId);
	Optional<ContaCorrente> findByAgencia_IdAgencyAndNumero(int agenciaId, int numero);
}
