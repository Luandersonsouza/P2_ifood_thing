// Cliente.java
package com.myfood.model;

public class Cliente extends Usuario {
    public Cliente() {}

    public Cliente(String nome, String email, String senha, String endereco) {
        super(nome, email, senha, endereco);
    }

    @Override
    public String getTipo() {
        return "cliente";
    }
}