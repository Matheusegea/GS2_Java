package br.com.sinalis.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "SINALIS_TRADUCAO")
public class Traducao {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "traducao_seq")
    @SequenceGenerator(name = "traducao_seq", sequenceName = "SINALIS_TRADUCAO_SEQ", allocationSize = 1)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "SESSAO_ID", nullable = false)
    private SessaoComunicacao sessao;
    @NotNull @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private Direcao direcao;
    @NotBlank @Size(max = 2000) @Column(nullable = false, length = 2000)
    private String texto;
    @Column(nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();
    public enum Direcao { PORTUGUES_PARA_LIBRAS, LIBRAS_PARA_PORTUGUES }
    public Long getId() { return id; }
    public SessaoComunicacao getSessao() { return sessao; }
    public void setSessao(SessaoComunicacao sessao) { this.sessao = sessao; }
    public Direcao getDirecao() { return direcao; }
    public void setDirecao(Direcao direcao) { this.direcao = direcao; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
}
