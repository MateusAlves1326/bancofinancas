package acc.br.bancofinancas.repository;

import org.springframework.data.repository.CrudRepository;

import acc.br.bancofinancas.model.BancoFinancas;

public interface BancoFinancasRepository extends CrudRepository<BancoFinancas, Integer> {
}

// Avia mais informações sobre o repositório do Banco Finanças, como métodos
// personalizados para consultas específicas, caso necessário.
