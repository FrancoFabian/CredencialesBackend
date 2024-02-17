package com.mx.credenciales.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredentialsWithFiles {
    @NotNull
    private Long numEmpleado;
    @NotBlank(message = "El campo no puede estar vacio")
    private String nombre;
    @NotNull
    private MultipartFile foto;
    @NotBlank(message = "El campo no puede estar vacio")
    private String categoria;
    @NotNull
    private MultipartFile firma;
    @NotNull
    private MultipartFile svg;
}
