package br.com.sinalis.bo;
import br.com.sinalis.dao.TraducaoDAO;
import br.com.sinalis.dto.TraducaoRequest;
import br.com.sinalis.exception.*;
import br.com.sinalis.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class TraducaoBO {
    private final TraducaoDAO dao;
    private final SessaoBO sessaoBO;
    public TraducaoBO(TraducaoDAO dao, SessaoBO sessaoBO) {
        this.dao = dao;
        this.sessaoBO = sessaoBO;
    }
    public List<Traducao> listar(Long sessaoId) {
        return sessaoId == null ? dao.findAll() : dao.findBySessaoIdOrderByCriadaEmDesc(sessaoId);
    }
    public Traducao buscar(Long id) {
        return dao.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Registro de tradução não encontrado"));
    }
    @Transactional
    public Traducao criar(TraducaoRequest req) { return dao.save(preencher(new Traducao(), req)); }
    @Transactional
    public Traducao atualizar(Long id, TraducaoRequest req) { return dao.save(preencher(buscar(id), req)); }
    private Traducao preencher(Traducao traducao, TraducaoRequest req) {
        SessaoComunicacao sessao = sessaoBO.buscar(req.sessaoId());
        if (sessao.getEncerradaEm() != null) {
            throw new RegraNegocioException("Não é possível registrar ou alterar traduções em sessão encerrada");
        }
        traducao.setSessao(sessao);
        traducao.setDirecao(req.direcao());
        traducao.setTexto(req.texto().trim());
        return traducao;
    }
    @Transactional
    public void excluir(Long id) { dao.delete(buscar(id)); }
}
