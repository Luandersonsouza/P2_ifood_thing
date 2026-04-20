// PedidoService.java
package com.myfood.service;

import com.myfood.exception.MyFoodException;
import com.myfood.model.*;
import com.myfood.persistence.Database;
import java.util.ArrayList;
import java.util.List;

public class PedidoService {
    private Database db = Database.getInstance();
    private UsuarioService usuarioService = new UsuarioService();
    private EmpresaService empresaService = new EmpresaService();
    private ProdutoService produtoService = new ProdutoService();

    public int criarPedido(int idCliente, int idEmpresa) {
        Usuario cliente = usuarioService.getUsuario(idCliente);
        if (cliente instanceof DonoEmpresa) {
            throw new MyFoodException("Dono de empresa nao pode fazer um pedido");
        }

        // Verifica se empresa existe
        empresaService.getAtributoEmpresa(idEmpresa, "nome");

        // Verifica se já existe pedido em aberto entre cliente e empresa
        boolean pedidoAberto = db.getPedidos().stream()
                .anyMatch(p -> p.getIdCliente() == idCliente &&
                        p.getIdEmpresa() == idEmpresa &&
                        p.getEstado() == EstadoPedido.ABERTO);
        if (pedidoAberto) {
            throw new MyFoodException("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
        }

        Pedido pedido = new Pedido(idCliente, idEmpresa);
        db.addPedido(pedido);
        return pedido.getNumero();
    }

    public void adicionarProduto(int numeroPedido, int idProduto) {
        Pedido pedido = getPedido(numeroPedido);
        if (pedido.getEstado() != EstadoPedido.ABERTO) {
            throw new MyFoodException("Nao e possivel adcionar produtos a um pedido fechado");
        }

        Produto produto = produtoService.getProdutoById(idProduto);
        if (produto.getIdEmpresa() != pedido.getIdEmpresa()) {
            throw new MyFoodException("O produto nao pertence a essa empresa");
        }

        pedido.getProdutos().add(idProduto);
        db.updatePedido(pedido);
    }

    public String getPedidos(int numeroPedido, String atributo) {
        Pedido pedido = getPedido(numeroPedido);
        Usuario cliente = usuarioService.getUsuario(pedido.getIdCliente());
        Empresa empresa = db.getEmpresas().stream()
                .filter(e -> e.getId() == pedido.getIdEmpresa())
                .findFirst().orElseThrow(() -> new MyFoodException("Empresa nao encontrada"));

        switch (atributo.toLowerCase()) {
            case "cliente": return cliente.getNome();
            case "empresa": return empresa.getNome();
            case "estado": return pedido.getEstado().name().toLowerCase();
            case "produtos":
                List<String> nomes = new ArrayList<>();
                for (int idProd : pedido.getProdutos()) {
                    Produto p = produtoService.getProdutoById(idProd);
                    nomes.add(p.getNome());
                }
                return "[" + String.join(", ", nomes) + "]";
            case "valor":
                float total = 0;
                for (int idProd : pedido.getProdutos()) {
                    Produto p = produtoService.getProdutoById(idProd);
                    total += p.getValor();
                }
                return String.format("%.2f", total).replace(',', '.');
            default:
                throw new MyFoodException("Atributo nao existe");
        }
    }

    public void fecharPedido(int numeroPedido) {
        Pedido pedido = getPedido(numeroPedido);
        if (pedido.getEstado() != EstadoPedido.ABERTO) {
            throw new MyFoodException("Pedido ja esta fechado");
        }
        pedido.setEstado(EstadoPedido.PREPARANDO);
        db.updatePedido(pedido);
    }

    public void removerProduto(int numeroPedido, String nomeProduto) {
        Pedido pedido = getPedido(numeroPedido);
        if (pedido.getEstado() != EstadoPedido.ABERTO) {
            throw new MyFoodException("Nao e possivel remover produtos de um pedido fechado");
        }
        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new MyFoodException("Produto invalido");
        }

        // Encontra o primeiro produto com o nome e remove
        for (int i = 0; i < pedido.getProdutos().size(); i++) {
            int idProd = pedido.getProdutos().get(i);
            Produto p = produtoService.getProdutoById(idProd);
            if (p.getNome().equals(nomeProduto)) {
                pedido.getProdutos().remove(i);
                db.updatePedido(pedido);
                return;
            }
        }
        throw new MyFoodException("Produto nao encontrado");
    }

    public int getNumeroPedido(int idCliente, int idEmpresa, int indice) {
        if (indice < 0) throw new MyFoodException("Indice invalido");

        List<Pedido> pedidos = db.getPedidos().stream()
                .filter(p -> p.getIdCliente() == idCliente && p.getIdEmpresa() == idEmpresa)
                .sorted((p1, p2) -> Integer.compare(p1.getNumero(), p2.getNumero()))
                .toList();

        if (indice >= pedidos.size()) {
            throw new MyFoodException("Indice maior que o esperado");
        }
        return pedidos.get(indice).getNumero();
    }

    private Pedido getPedido(int numero) {
        return db.getPedidos().stream()
                .filter(p -> p.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Pedido nao encontrado"));
    }

    public void liberarPedido(int numero) {
        Pedido pedido = getPedido(numero);
        if (pedido.getEstado() != EstadoPedido.PREPARANDO) {
            throw new MyFoodException("Pedido nao pode ser liberado");
        }
        pedido.setEstado(EstadoPedido.LIBERADO);
        db.updatePedido(pedido);
    }
}