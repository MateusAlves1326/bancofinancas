package acc.br.bancofinancas.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import acc.br.bancofinancas.dto.CreateContaCorrenteRequest;
import acc.br.bancofinancas.service.ContaCorrenteService;

@ExtendWith(MockitoExtension.class)
class ContaCorrenteControllerTest {

    @Mock
    private ContaCorrenteService contaCorrenteService;

    @InjectMocks
    private ContaCorrenteController contaCorrenteController;

    @Test
    void createDeveDelegarParaService() {
        CreateContaCorrenteRequest request = new CreateContaCorrenteRequest();

        contaCorrenteController.create(request);

        verify(contaCorrenteService).createContaCorrente(request);
    }
}
