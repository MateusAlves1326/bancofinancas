package acc.br.bancofinancas.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.Agencia;

public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {
    List<Agencia> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCaseOrderByNameAsc(String name, String address);
    List<Agencia> findAllByOrderByNameAsc();
    Optional<Agencia> findByName(String name);
}
