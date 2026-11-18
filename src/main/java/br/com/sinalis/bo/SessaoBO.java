package br.com.sinalis.bo;
import br.com.sinalis.dao.*;
import br.com.sinalis.exception.*;
import br.com.sinalis.model.SessaoComunicacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class SessaoBO {
    private final SessaoDAO dao;
    private final TraducaoDAO traducaoDAO;
    private final UsuarioBO usuarioBO;
    public SessaoBO(SessaoDAO dao, TraducaoDAO traducaoDAO, UsuarioBO usuarioBO) {
        this.dao = dao;
        this.traducaoDAO = traducaoDAO;
        this.usuarioBO = usuarioBO;
    }
    public List<SessaoComunicacao> listar() { return dao.findAll(); }
    public SessaoComunicacao buscar(Long id) {
        return dao.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Sessão não encontrada"));
    }
    @Transactional
    public SessaoComunicacao criar(Long usuarioId) {
        SessaoComunicacao sessao = new SessaoComunicacao();
        sessao.setUsuario(usuarioBO.buscar(usuarioId));
        return dao.save(sessao);
    }
    @Transactional
    public SessaoComunicacao encerrar(Long id) {
        SessaoComunicacao sessao = buscar(id);
        if (sessao.getEncerradaEm() != null) {
            throw new RegraNegocioException("Sessão já encerrada");
        }
        sessao.encerrar();
        return dao.save(sessao);
    }
    @Transactional
    public void excluir(Long id) {
        buscar(id);
        if (traducaoDAO.existsBySessaoId(id)) {
            throw new RegraNegocioException("Exclua as traduções da sessão antes de removê-la");
        }
        dao.deleteById(id);
    }
}
