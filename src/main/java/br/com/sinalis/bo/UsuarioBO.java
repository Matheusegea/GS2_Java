package br.com.sinalis.bo;
import br.com.sinalis.dao.*;
import br.com.sinalis.dto.UsuarioRequest;
import br.com.sinalis.exception.*;
import br.com.sinalis.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class UsuarioBO {
    private final UsuarioDAO dao;
    private final SessaoDAO sessaoDAO;
    public UsuarioBO(UsuarioDAO dao, SessaoDAO sessaoDAO) {
        this.dao = dao;
        this.sessaoDAO = sessaoDAO;
    }
    public List<Usuario> listar() { return dao.findAll(); }
    public Usuario buscar(Long id) {
        return dao.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
    @Transactional
    public Usuario criar(UsuarioRequest req) { return dao.save(preencher(new Usuario(), req)); }
    @Transactional
    public Usuario atualizar(Long id, UsuarioRequest req) { return dao.save(preencher(buscar(id), req)); }
    private Usuario preencher(Usuario usuario, UsuarioRequest req) {
        usuario.setNome(req.nome().trim());
        usuario.setEmail(req.email().trim().toLowerCase());
        usuario.setPerfil(req.perfil());
        return usuario;
    }
    @Transactional
    public void excluir(Long id) {
        buscar(id);
        if (sessaoDAO.existsByUsuarioId(id)) {
            throw new RegraNegocioException("Exclua as sessões do usuário antes de removê-lo");
        }
        dao.deleteById(id);
    }
}
