// Validador.java
package com.myfood.util;

public class Validador {
    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email invalido");
        }
    }

    public static void validarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha invalido");
        }
    }

    public static void validarEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereco invalido");
        }
    }

    public static void validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 14) {
            throw new IllegalArgumentException("CPF invalido");
        }
        // formato esperado: XXX.XXX.XXX-XX
        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("CPF invalido");
        }
    }

    public static void validarValor(float valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor invalido");
        }
    }

    public static void validarCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria invalido");
        }
    }
}