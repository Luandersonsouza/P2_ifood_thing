// Pedido.java
package MyFood.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private int numero;
    private int idCliente;
    private int idEmpresa;
    private EstadoPedido estado;
    private List<Integer> produtos = new ArrayList<>();

    public Pedido() {}

    public Pedido(int idCliente, int idEmpresa) {
        this.idCliente = idCliente;
        this.idEmpresa = idEmpresa;
        this.estado = EstadoPedido.ABERTO;
    }

    // Getters e Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public List<Integer> getProdutos() { return produtos; }
    public void setProdutos(List<Integer> produtos) { this.produtos = produtos; }
}