package acc.br.bancofinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import acc.br.bancofinancas.model.Agente;

public interface AgenteRepository extends JpaRepository<Agente, Integer> {

    boolean existsByMatricula(String matricula);
}