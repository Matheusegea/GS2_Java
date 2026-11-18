package br.com.sinalis.dao;
import br.com.sinalis.model.Traducao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TraducaoDAO extends JpaRepository<Traducao, Long> {
    java.util.List<Traducao> findBySessaoIdOrderByCriadaEmDesc(Long sessaoId);
    boolean existsBySessaoId(Long sessaoId);
}
