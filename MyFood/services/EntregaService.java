package MyFood.services;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;
import java.util.Comparator;
import java.util.List;

public class EntregaService {
    private Database db = Database.getInstance();
    private UsuarioService usuarioService = new UsuarioService();
    private ProdutoService produtoService = new ProdutoService();

    public int criarEntrega(int numeroPedido, int idEntregador, String destino) {
        Pedido pedido = getPedido(numeroPedido);

        if (pedido.getEstado() != EstadoPedido.PRONTO) {
            throw new MyFoodException("Pedido nao esta pronto para entrega");
        }

        Usuario entregador = usuarioService.getUsuario(idEntregador);
        if (!(entregador instanceof Entregador)) {
            throw new MyFoodException("Nao e um entregador valido");
        }
        if (entregadorEstaEmEntrega(idEntregador)) {
            throw new MyFoodException("Entregador ainda em entrega");
        }

        Empresa empresa = getEmpresa(pedido.getIdEmpresa());
        if (!empresa.getEntregadores().contains(idEntregador)) {
            throw new MyFoodException("Nao e um entregador valido");
        }

        String destinoEntrega = destino;
        if (destinoEntrega == null || destinoEntrega.trim().isEmpty()) {
            destinoEntrega = usuarioService.getUsuario(pedido.getIdCliente()).getEndereco();
        }

        Entrega entrega = new Entrega(numeroPedido, idEntregador, destinoEntrega);
        db.addEntrega(entrega);

        pedido.setEstado(EstadoPedido.ENTREGANDO);
        db.updatePedido(pedido);

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

        Pedido pedido = getPedido(entrega.getIdPedido());
        switch (atributo.toLowerCase()) {
            case "cliente":
                return usuarioService.getUsuario(pedido.getIdCliente()).getNome();
            case "empresa":
                return getEmpresa(pedido.getIdEmpresa()).getNome();
            case "pedido":
                return String.valueOf(entrega.getIdPedido());
            case "entregador":
                return usuarioService.getUsuario(entrega.getIdEntregador()).getNome();
            case "destino":
                return entrega.getDestino();
            case "produtos":
                return getProdutosEntrega(pedido);
            default:
                throw new MyFoodException("Atributo nao existe");
        }
    }

    public int getIdEntrega(int numeroPedido) {
        return db.getEntregas().stream()
                .filter(e -> e.getIdPedido() == numeroPedido)
                .findFirst()
                .map(Entrega::getId)
                .orElseThrow(() -> new MyFoodException("Nao existe entrega com esse id"));
    }

    public void entregar(int idEntrega) {
        Entrega entrega = db.getEntregas().stream()
                .filter(e -> e.getId() == idEntrega)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Nao existe nada para ser entregue com esse id"));

        entrega.setEntregue(true);
        db.updateEntrega(entrega);

        Pedido pedido = getPedido(entrega.getIdPedido());
        pedido.setEstado(EstadoPedido.ENTREGUE);
        db.updatePedido(pedido);
    }

    public int obterPedido(int idEntregador) {
        Usuario entregador = usuarioService.getUsuario(idEntregador);
        if (!(entregador instanceof Entregador)) {
            throw new MyFoodException("Usuario nao e um entregador");
        }

        List<Empresa> empresas = db.getEmpresas().stream()
                .filter(e -> e.getEntregadores().contains(idEntregador))
                .toList();
        if (empresas.isEmpty()) {
            throw new MyFoodException("Entregador nao estar em nenhuma empresa.");
        }

        return db.getPedidos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
                .filter(p -> getEmpresa(p.getIdEmpresa()).getEntregadores().contains(idEntregador))
                .filter(p -> db.getEntregas().stream().noneMatch(e -> e.getIdPedido() == p.getNumero() && !e.isEntregue()))
                .sorted(Comparator
                        .comparing((Pedido p) -> !(getEmpresa(p.getIdEmpresa()) instanceof Farmacia))
                        .thenComparingInt(Pedido::getNumero))
                .map(Pedido::getNumero)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Nao existe pedido para entrega"));
    }

    private boolean entregadorEstaEmEntrega(int idEntregador) {
        return db.getEntregas().stream()
                .anyMatch(e -> e.getIdEntregador() == idEntregador && !e.isEntregue());
    }

    private Pedido getPedido(int numeroPedido) {
        return db.getPedidos().stream()
                .filter(p -> p.getNumero() == numeroPedido)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Pedido nao encontrado"));
    }

    private Empresa getEmpresa(int idEmpresa) {
        return db.getEmpresas().stream()
                .filter(e -> e.getId() == idEmpresa)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Empresa nao cadastrada"));
    }

    private String getProdutosEntrega(Pedido pedido) {
        List<String> nomes = pedido.getProdutos().stream()
                .map(idProduto -> produtoService.getProdutoById(idProduto).getNome())
                .toList();
        return "{[" + String.join(", ", nomes) + "]}";
    }
}
