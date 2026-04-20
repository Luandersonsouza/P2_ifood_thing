// UsuarioService.java
package com.myfood.service;

import com.myfood.exception.MyFoodException;
import com.myfood.model.*;
import com.myfood.persistence.Database;
import com.myfood.util.Validador;

public class UsuarioService {
    private Database db = Database.getInstance();

    public void criarCliente(String nome, String email, String senha, String endereco) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);

        // Verifica email único
        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }

        Cliente cliente = new Cliente(nome, email, senha, endereco);
        db.addUsuario(cliente);
    }

    public void criarDonoEmpresa(String nome, String email, String senha, String endereco, String cpf) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);
        Validador.validarCpf(cpf);

        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }

        DonoEmpresa dono = new DonoEmpresa(nome, email, senha, endereco, cpf);
        db.addUsuario(dono);
    }

    public void criarEntregador(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);
        if (veiculo == null || veiculo.trim().isEmpty()) throw new IllegalArgumentException("Veiculo invalido");
        if (placa == null || placa.trim().isEmpty()) throw new IllegalArgumentException("Placa invalida");

        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }

        Entregador entregador = new Entregador(nome, email, senha, endereco, veiculo, placa);
        db.addUsuario(entregador);
    }

    public int login(String email, String senha) {
        Usuario usuario = db.getUsuarios().stream()
                .filter(u -> u.getEmail().equals(email) && u.getSenha().equals(senha))
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Login ou senha invalidos"));
        return usuario.getId();
    }

    public String getAtributoUsuario(int id, String atributo) {
        Usuario usuario = db.getUsuarios().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Usuario nao cadastrado."));

        switch (atributo.toLowerCase()) {
            case "nome": return usuario.getNome();
            case "email": return usuario.getEmail();
            case "senha": return usuario.getSenha();
            case "endereco": return usuario.getEndereco();
            case "cpf":
                if (usuario instanceof DonoEmpresa donoempresa) {
                    return donoempresa.getCpf();
                }
                throw new MyFoodException("Atributo invalido");
            case "veiculo":
                if (usuario instanceof Entregador entregador) {
                    return entregador.getVeiculo();
                }
                throw new MyFoodException("Atributo invalido");
            case "placa":
                if (usuario instanceof Entregador entregador) {
                    return entregador.getPlaca();
                }
                throw new MyFoodException("Atributo invalido");
            default:
                throw new MyFoodException("Atributo invalido");
        }
    }

    public Usuario getUsuario(int id) {
        return db.getUsuarios().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Usuario nao cadastrado."));
    }
}