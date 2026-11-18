package br.com.sinalis.dto;
import jakarta.validation.constraints.NotNull;
public record SessaoRequest(@NotNull Long usuarioId) {}
