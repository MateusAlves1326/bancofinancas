package acc.br.bancofinancas.repository;

import acc.br.bancofinancas.model.PedidoLoja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoLojaRepository extends JpaRepository<PedidoLoja, Integer> {
    List<PedidoLoja> findByCliente_IdCustomerOrderByDataCriacaoDesc(int clienteId);
    Optional<PedidoLoja> findByCodigoPagamento(String codigoPagamento);
}