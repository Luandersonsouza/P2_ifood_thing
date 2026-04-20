// Produto.java
package com.myfood.model;

import java.io.Serializable;

public class Produto implements Serializable {
    private int id;
    private int idEmpresa;
    private String nome;
    private float valor;
    private String categoria;

    public Produto() {}

    public Produto(int idEmpresa, String nome, float valor, String categoria) {
        this.idEmpresa = idEmpresa;
        this.nome = nome;
        this.valor = valor;
        this.categoria = categoria;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}