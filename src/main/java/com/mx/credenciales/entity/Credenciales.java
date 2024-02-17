package com.mx.credenciales.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CREDENCIALES")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credenciales {
    @Id
    @Column
    private Long numEmpleado;
    @Column
    private String nombre;
    @Column
    private  String foto;
    @Column
    private String categoria;
    @Column
    private String firma;
    @Column
    private String pdf;
    @Column
    private  String visualizacion;
}
