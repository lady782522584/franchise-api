package com.franchise.api.dto;

import jakarta.validation.constraints.NotBlank;

public record NameRequest(@NotBlank(message = "El nombre es obligatorio") String name) {
}