package br.com.sinalis.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/sinais")
public class SinalController {
    private final List<Sinal> sinais;
    private final Map<String, Sinal> indice = new HashMap<>();

    public SinalController(ObjectMapper mapper) throws IOException {
        try (var entrada = new ClassPathResource("sinais.json").getInputStream()) {
            List<Sinal> entradas = mapper.readValue(entrada, new TypeReference<List<Sinal>>() {});
            Map<String, Sinal> unicos = new LinkedHashMap<>();
            for (Sinal sinal : entradas) {
                unicos.putIfAbsent(sinal.imagem(), sinal);
                String termo = normalizar(String.join(" ", sinal.termos()));
                indice.put(termo, sinal);
            }
            sinais = List.copyOf(unicos.values());
        }
    }

    @GetMapping
    public List<Sinal> listar() {
        return sinais;
    }

    @PostMapping("/converter")
    public Resultado converter(@RequestBody FraseRequest requisicao) {
        if (requisicao == null || requisicao.texto() == null || requisicao.texto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Digite uma frase para converter.");
        }
        if (requisicao.texto().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A frase deve ter até 500 caracteres.");
        }
        String texto = normalizar(requisicao.texto());
        if (texto.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Digite palavras válidas.");
        }
        // Abreviaturas comuns usadas nas mensagens.
        texto = texto.replaceAll("\\bvc\\b", "voce");

        // Na pergunta "tudo bem com voce", "com" não exige uma imagem
        // isolada. Exibimos a sequência OI -> VOCE -> TUDO BEM.
        texto = texto.replaceAll("\\btudo bem com voce\\b", "voce tudo bem");
        String[] palavras = texto.split("\\s+");
        List<Sinal> encontrados = new ArrayList<>();
        List<String> desconhecidos = new ArrayList<>();
        for (int i = 0; i < palavras.length;) {
            Sinal sinal = null;
            int consumidos = 0;
            for (int tamanho = Math.min(3, palavras.length - i); tamanho >= 1; tamanho--) {
                String chave = String.join(" ", Arrays.copyOfRange(palavras, i, i + tamanho));
                if (indice.containsKey(chave)) {
                    sinal = indice.get(chave);
                    consumidos = tamanho;
                    break;
                }
            }
            if (sinal != null) {
                encontrados.add(sinal);
                i += consumidos;
            } else {
                desconhecidos.add(palavras[i]);
                i++;
            }
        }
        return new Resultado(encontrados, desconhecidos, sinais.size());
    }

    private static String normalizar(String texto) {
        String semAcentos = Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return Pattern.compile("[^a-z0-9 ]+").matcher(semAcentos).replaceAll(" ")
                .trim().replaceAll(" +", " ");
    }

    public record Sinal(String rotulo, String imagem, List<String> termos) {}
    public record FraseRequest(String texto) {}
    public record Resultado(List<Sinal> sinais, List<String> palavrasNaoEncontradas, int totalSinaisDisponiveis) {}
}
