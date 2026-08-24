package acc.br.bancofinancas.config;

import acc.br.bancofinancas.model.LojaItem;
import acc.br.bancofinancas.model.ContaCorrente;
import acc.br.bancofinancas.model.Extrato;
import acc.br.bancofinancas.model.Operacao;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.SolicitacaoReversao;
import acc.br.bancofinancas.model.StatusSolicitacaoReversao;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.ContaCorrenteRepository;
import acc.br.bancofinancas.repository.ExtratoRepository;
import acc.br.bancofinancas.repository.LojaItemRepository;
import acc.br.bancofinancas.repository.SolicitacaoReversaoRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CargaInicialLoja implements CommandLineRunner {

    private final LojaItemRepository lojaItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContaCorrenteRepository contaCorrenteRepository;
    private final ExtratoRepository extratoRepository;
    private final SolicitacaoReversaoRepository solicitacaoReversaoRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicialLoja(
            LojaItemRepository lojaItemRepository,
            UsuarioRepository usuarioRepository,
            ContaCorrenteRepository contaCorrenteRepository,
            ExtratoRepository extratoRepository,
            SolicitacaoReversaoRepository solicitacaoReversaoRepository,
            PasswordEncoder passwordEncoder) {
        this.lojaItemRepository = lojaItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.contaCorrenteRepository = contaCorrenteRepository;
        this.extratoRepository = extratoRepository;
        this.solicitacaoReversaoRepository = solicitacaoReversaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarUsuarioLojaSeNecessario();
        criarDadosDemonstracaoSeNecessario();

        if (lojaItemRepository.count() == 0) {
            lojaItemRepository.saveAll(List.of(
                    criarItem("Fone Bluetooth", "Fone sem fio com cancelamento de ruído", new BigDecimal("249.90"), 25),
                    criarItem("Smartwatch", "Relógio inteligente com monitor cardíaco", new BigDecimal("399.90"), 18),
                    criarItem("Mochila Executiva", "Mochila resistente para notebook", new BigDecimal("189.90"), 30),
                    criarItem("Caixa de Som", "Caixa de som portátil à prova d'água", new BigDecimal("299.90"), 20),
                    criarItem("Gift Card", "Cartão presente digital", new BigDecimal("100.00"), 50)
            ));
        }
    }

    private void criarDadosDemonstracaoSeNecessario() {
        if (extratoRepository.count() > 0) {
            return;
        }

        ContaCorrente conta = contaCorrenteRepository.findAll().stream().findFirst().orElse(null);
        if (conta == null) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        Extrato deposito = criarExtrato(conta, Operacao.DEPOSITO, new BigDecimal("1500.00"), agora.minusDays(3));
        Extrato saque = criarExtrato(conta, Operacao.SAQUE, new BigDecimal("200.00"), agora.minusDays(2));
        Extrato pagamento = criarExtrato(conta, Operacao.PAGAMENTO, new BigDecimal("89.90"), agora.minusDays(1));
        extratoRepository.saveAll(List.of(deposito, saque, pagamento));

        SolicitacaoReversao solicitacao = new SolicitacaoReversao();
        solicitacao.setExtratoOrigem(deposito);
        solicitacao.setContaCorrente(conta);
        solicitacao.setValor(deposito.getValorOperacao());
        solicitacao.setOperacaoReversa(Operacao.ESTORNO_DEPOSITO);
        solicitacao.setStatus(StatusSolicitacaoReversao.PENDENTE);
        solicitacao.setMotivo("Solicitação de demonstração");
        solicitacao.setDataSolicitacao(agora);
        solicitacaoReversaoRepository.save(solicitacao);
    }

    private Extrato criarExtrato(
            ContaCorrente conta, Operacao operacao, BigDecimal valor, LocalDateTime dataHoraMovimento) {
        Extrato extrato = new Extrato();
        extrato.setContaCorrente(conta);
        extrato.setOperacao(operacao);
        extrato.setValorOperacao(valor);
        extrato.setDataHoraMovimento(dataHoraMovimento);
        return extrato;
    }

    private LojaItem criarItem(String nome, String descricao, BigDecimal preco, int estoque) {
        LojaItem item = new LojaItem();
        item.setNome(nome);
        item.setDescricao(descricao);
        item.setPreco(preco);
        item.setEstoque(estoque);
        item.setAtivo(true);
        return item;
    }

    private void criarUsuarioLojaSeNecessario() {
        if (usuarioRepository.existsByUsername("LOJA")) {
            return;
        }

        Usuario loja = new Usuario();
        loja.setUsername("LOJA");
        loja.setPassword(passwordEncoder.encode("1234"));
        loja.setRole(Role.LOJA);
        loja.setClienteId(null);
        loja.setAgenciaId(null);
        usuarioRepository.save(loja);
    }
}