// EntregaService.java
package MyFood.services;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;
import java.util.List;

public class EntregaService {
    private Database db = Database.getInstance();
    private UsuarioService usuarioService = new UsuarioService();

    public int criarEntrega(int numeroPedido, int idEntregador, String destino) {
        Pedido pedido = db.getPedidos().stream()
                .filter(p -> p.getNumero() == numeroPedido)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Pedido nao encontrado"));

        if (pedido.getEstado() != EstadoPedido.LIBERADO) {
            throw new MyFoodException("Pedido nao esta liberado para entrega");
        }

        Usuario entregador = usuarioService.getUsuario(idEntregador);
        if (!(entregador instanceof Entregador)) {
            throw new MyFoodException("Usuario nao e entregador");
        }

        Entrega entrega = new Entrega(numeroPedido, idEntregador, destino);
        db.addEntrega(entrega);
        return entrega.getId();
    }

    public String getEntrega(int idEntrega, String atributo) {
        Entrega entrega = db.getEntregas().stream()
                .filter(e -> e.getId() == idEntrega)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Entrega nao encontrada"));

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new MyFoodException("Atributo invalido");
        }

        switch (atributo.toLowerCase()) {
            case "destino": return entrega.getDestino();
            case "entregue": return String.valueOf(entrega.isEntregue());
            default: throw new MyFoodException("Atributo invalido");
        }
    }

    public int getIdEntrega(int numeroPedido) {
        return db.getEntregas().stream()
                .filter(e -> e.getIdPedido() == numeroPedido)
                .findFirst()
                .map(Entrega::getId)
                .orElseThrow(() -> new MyFoodException("Entrega nao encontrada para o pedido"));
    }

    public void entregar(int idEntrega) {
        Entrega entrega = db.getEntregas().stream()
                .filter(e -> e.getId() == idEntrega)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Entrega nao encontrada"));
        entrega.setEntregue(true);
        db.updateEntrega(entrega);

        Pedido pedido = db.getPedidos().stream()
                .filter(p -> p.getNumero() == entrega.getIdPedido())
                .findFirst().orElse(null);
        if (pedido != null) {
            pedido.setEstado(EstadoPedido.ENTREGUE);
            db.updatePedido(pedido);
        }
    }

    public int obterPedido(int idEntregador) {
        // Retorna o número do primeiro pedido liberado que ainda não tem entrega associada
        // e que o entregador está cadastrado na empresa (ou não? Conforme especificação, vamos simplificar)
        List<Pedido> pedidosLiberados = db.getPedidos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.LIBERADO)
                .toList();

        for (Pedido p : pedidosLiberados) {
            boolean temEntrega = db.getEntregas().stream().anyMatch(e -> e.getIdPedido() == p.getNumero());
            if (!temEntrega) {
                // Verifica se entregador está associado à empresa
                Empresa empresa = db.getEmpresas().stream()
                        .filter(e -> e.getId() == p.getIdEmpresa())
                        .findFirst().orElse(null);
                if (empresa != null && empresa.getEntregadores().contains(idEntregador)) {
                    return p.getNumero();
                }
            }
        }
        throw new MyFoodException("Nenhum pedido disponivel");
    }
}
