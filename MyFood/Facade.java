package MyFood;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;
import MyFood.services.*;

import java.util.List;
import java.util.stream.Collectors;

public class Facade {
    private Database db = Database.getInstance();
    private UsuarioService usuarioService = new UsuarioService();
    private EmpresaService empresaService = new EmpresaService();
    private ProdutoService produtoService = new ProdutoService();
    private PedidoService pedidoService = new PedidoService();
    private EntregaService entregaService = new EntregaService();

    public void zerarSistema() { db.zerar(); }
    public void encerrarSistema() { }

    // criarUsuario (cliente)
    public void criarUsuario(String nome, String email, String senha, String endereco) {
        usuarioService.criarCliente(nome, email, senha, endereco);
    }
    // criarUsuario (dono)
    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        usuarioService.criarDonoEmpresa(nome, email, senha, endereco, cpf);
    }
    // criarUsuario (entregador)
    public void criarUsuario(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        usuarioService.criarEntregador(nome, email, senha, endereco, veiculo, placa);
    }

    public int login(String email, String senha) { return usuarioService.login(email, senha); }
    public String getAtributoUsuario(int id, String atributo) { return usuarioService.getAtributoUsuario(id, atributo); }

    // criarEmpresa (restaurante)
    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha) {
        if (!"restaurante".equalsIgnoreCase(tipoEmpresa)) throw new MyFoodException("Tipo de empresa invalido");
        return empresaService.criarRestaurante(dono, nome, endereco, tipoCozinha);
    }
    // criarEmpresa (mercado)
    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String abre, String fecha, String tipoMercado) {
        if (!"mercado".equalsIgnoreCase(tipoEmpresa)) throw new MyFoodException("Tipo de empresa invalido");
        return empresaService.criarMercado(dono, nome, endereco, abre, fecha, tipoMercado);
    }
    // criarEmpresa (farmacia)
    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        if (!"farmacia".equalsIgnoreCase(tipoEmpresa)) throw new MyFoodException("Tipo de empresa invalido");
        return empresaService.criarFarmacia(dono, nome, endereco, aberto24Horas, numeroFuncionarios);
    }

    public String getEmpresasDoUsuario(int idDono) { return empresaService.getEmpresasDoUsuario(idDono); }
    public String getAtributoEmpresa(int empresa, String atributo) { return empresaService.getAtributoEmpresa(empresa, atributo); }
    public int getIdEmpresa(int idDono, String nome, int indice) { return empresaService.getIdEmpresa(idDono, nome, indice); }
    public void alterarFuncionamento(int mercado, String abre, String fecha) { empresaService.alterarFuncionamento(mercado, abre, fecha); }
    public void cadastrarEntregador(int empresa, int entregador) { empresaService.cadastrarEntregador(empresa, entregador); }

    public String getEntregadores(int empresa) {
        List<String> entregadores = empresaService.getEntregadores(empresa);
        return "{[" + String.join(", ", entregadores) + "]}";
    }

    // Ajuste para retornar a String formatada conforme os testes
    public String getEmpresas(int entregador) {
        List<Empresa> emps = empresaService.getEmpresasDoEntregador(entregador);
        StringBuilder sb = new StringBuilder("{");
        sb.append("[");
        for (int i = 0; i < emps.size(); i++) {
            Empresa e = emps.get(i);
            sb.append("[").append(e.getNome()).append(", ").append(e.getEndereco()).append("]");
            if (i < emps.size() - 1) sb.append(", ");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public int criarProduto(int empresa, String nome, float valor, String categoria) { return produtoService.criarProduto(empresa, nome, valor, categoria); }
    public void editarProduto(int produto, String nome, float valor, String categoria) { produtoService.editarProduto(produto, nome, valor, categoria); }
    public String getProduto(String nome, int empresa, String atributo) { return produtoService.getProduto(nome, empresa, atributo); }
    public String listarProdutos(int empresa) { return produtoService.listarProdutos(empresa); }

    public int criarPedido(int cliente, int empresa) { return pedidoService.criarPedido(cliente, empresa); }
    public void adicionarProduto(int numero, int produto) { pedidoService.adicionarProduto(numero, produto); }
    public String getPedidos(int numero, String atributo) { return pedidoService.getPedidos(numero, atributo); }
    public void fecharPedido(int numero) { pedidoService.fecharPedido(numero); }
    public void removerProduto(int pedido, String produto) { pedidoService.removerProduto(pedido, produto); }
    public int getNumeroPedido(int cliente, int empresa, int indice) { return pedidoService.getNumeroPedido(cliente, empresa, indice); }
    public void liberarPedido(int numero) { pedidoService.liberarPedido(numero); }

    public int criarEntrega(int pedido, int entregador, String destino) { return entregaService.criarEntrega(pedido, entregador, destino); }
    public String getEntrega(int id, String atributo) { return entregaService.getEntrega(id, atributo); }
    public int getIdEntrega(int pedido) { return entregaService.getIdEntrega(pedido); }
    public void entregar(int entrega) { entregaService.entregar(entrega); }
    public int obterPedido(int entregador) { return entregaService.obterPedido(entregador); }
}
