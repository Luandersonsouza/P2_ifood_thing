// Entrega.java
package com.myfood.model;

import java.io.Serializable;

public class Entrega implements Serializable {
    private int id;
    private int idPedido;
    private int idEntregador;
    private String destino;
    private boolean entregue;

    public Entrega() {}

    public Entrega(int idPedido, int idEntregador, String destino) {
        this.idPedido = idPedido;
        this.idEntregador = idEntregador;
        this.destino = destino;
        this.entregue = false;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public int getIdEntregador() { return idEntregador; }
    public void setIdEntregador(int idEntregador) { this.idEntregador = idEntregador; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public boolean isEntregue() { return entregue; }
    public void setEntregue(boolean entregue) { this.entregue = entregue; }
}