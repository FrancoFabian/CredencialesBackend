package com.mx.credenciales.service;

import com.mx.credenciales.entity.Credenciales;
import com.mx.credenciales.request.CredentialsWithFiles;

import java.io.IOException;
import java.util.List;

public interface CredencialService {
    public List<Credenciales> listar();
    public Credenciales eliminar(Long id);
    public Credenciales guardar(CredentialsWithFiles credenciales)throws IOException;
    public Credenciales editar(Credenciales credenciales)throws IOException;

}
