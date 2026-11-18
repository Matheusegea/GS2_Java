package br.com.sinalis.dto;
import br.com.sinalis.model.Traducao;
import jakarta.validation.constraints.*;
public record TraducaoRequest(@NotNull Long sessaoId,
                              @NotNull Traducao.Direcao direcao,
                              @NotBlank @Size(max = 2000) String texto) {}
