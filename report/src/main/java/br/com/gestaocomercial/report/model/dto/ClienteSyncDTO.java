package br.com.gestaocomercial.report.model.dto;

import java.time.LocalDate;

public record ClienteSyncDTO(Integer id, String nome, String tipo, LocalDate dataCadastro,
                             String statusCliente, String cidade, String estado, String email) {}
