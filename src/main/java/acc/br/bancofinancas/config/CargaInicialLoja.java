package acc.br.bancofinancas.config;

import acc.br.bancofinancas.model.LojaItem;
import acc.br.bancofinancas.model.Role;
import acc.br.bancofinancas.model.Usuario;
import acc.br.bancofinancas.repository.LojaItemRepository;
import acc.br.bancofinancas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CargaInicialLoja implements CommandLineRunner {

    private final LojaItemRepository lojaItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicialLoja(
            LojaItemRepository lojaItemRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.lojaItemRepository = lojaItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarUsuarioLojaSeNecessario();

        if (lojaItemRepository.count() > 0) {
            return;
        }

        lojaItemRepository.saveAll(List.of(
                criarItem("Fone Bluetooth", "Fone sem fio com cancelamento de ruído", new BigDecimal("249.90"), 25),
                criarItem("Smartwatch", "Relógio inteligente com monitor cardíaco", new BigDecimal("399.90"), 18),
                criarItem("Mochila Executiva", "Mochila resistente para notebook", new BigDecimal("189.90"), 30),
                criarItem("Caixa de Som", "Caixa de som portátil à prova d'água", new BigDecimal("299.90"), 20),
                criarItem("Gift Card", "Cartão presente digital", new BigDecimal("100.00"), 50)
        ));
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