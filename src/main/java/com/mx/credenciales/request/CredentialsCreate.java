package com.mx.credenciales.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsCreate {
    private Long numEmpleado;
    @NotBlank(message = "El campo no puede estar vacio")
    private String nombre;
    @NotBlank(message = "El campo no puede estar vacio")
    private String categoria;
    @NotNull
    private MultipartFile svg;
}
