package acc.br.bancofinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.Agencia;

public interface AgenciaRepository extends JpaRepository<Agencia, Integer> {
}
