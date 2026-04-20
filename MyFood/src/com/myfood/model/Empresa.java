// Empresa.java
package com.myfood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Empresa implements Serializable {
    private int id;
    private String nome;
    private String endereco;
    private int idDono;
    private List<Integer> entregadores = new ArrayList<>();

    public Empresa() {}

    public Empresa(String nome, String endereco, int idDono) {
        this.nome = nome;
        this.endereco = endereco;
        this.idDono = idDono;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public int getIdDono() { return idDono; }
    public void setIdDono(int idDono) { this.idDono = idDono; }
    public List<Integer> getEntregadores() { return entregadores; }
    public void setEntregadores(List<Integer> entregadores) { this.entregadores = entregadores; }

    public abstract String getTipo();
}