package MyFood.services;

import MyFood.models.*;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class Database implements Serializable {
    private static Database instance;
    private static final String FILE_NAME = "myfood_data.xml";

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Empresa> empresas = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Entrega> entregas = new ArrayList<>();

    private int nextUsuarioId = 1;
    private int nextEmpresaId = 1;
    private int nextProdutoId = 1;
    private int nextPedidoNumero = 1;
    private int nextEntregaId = 1;

    private Database() { load(); }

    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    // Getters (retornam cópia defensiva)
    public List<Usuario> getUsuarios() { return new ArrayList<>(usuarios); }
    public List<Empresa> getEmpresas() { return new ArrayList<>(empresas); }
    public List<Produto> getProdutos() { return new ArrayList<>(produtos); }
    public List<Pedido> getPedidos() { return new ArrayList<>(pedidos); }
    public List<Entrega> getEntregas() { return new ArrayList<>(entregas); }

    // Adição com geração de ID e persistência
    public synchronized void addUsuario(Usuario u) { u.setId(nextUsuarioId++); usuarios.add(u); save(); }
    public synchronized void addEmpresa(Empresa e) { e.setId(nextEmpresaId++); empresas.add(e); save(); }
    public synchronized void addProduto(Produto p) { p.setId(nextProdutoId++); produtos.add(p); save(); }
    public synchronized void addPedido(Pedido p) { p.setNumero(nextPedidoNumero++); pedidos.add(p); save(); }
    public synchronized void addEntrega(Entrega e) { e.setId(nextEntregaId++); entregas.add(e); save(); }

    // Atualização
    public void updateUsuario(Usuario u) { replace(usuarios, u); save(); }
    public void updateEmpresa(Empresa e) { replace(empresas, e); save(); }
    public void updateProduto(Produto p) { replace(produtos, p); save(); }
    public void updatePedido(Pedido p) { replace(pedidos, p); save(); }
    public void updateEntrega(Entrega e) { replace(entregas, e); save(); }

    private <T> void replace(List<T> list, T obj) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(obj)) { list.set(i, obj); break; }
        }
    }

    public synchronized void zerar() {
        usuarios.clear(); empresas.clear(); produtos.clear(); pedidos.clear(); entregas.clear();
        nextUsuarioId = 1; nextEmpresaId = 1; nextProdutoId = 1; nextPedidoNumero = 1; nextEntregaId = 1;
        save();
    }

    private void save() {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(FILE_NAME)))) {
            encoder.writeObject(usuarios);
            encoder.writeObject(empresas);
            encoder.writeObject(produtos);
            encoder.writeObject(pedidos);
            encoder.writeObject(entregas);
            encoder.writeObject(nextUsuarioId);
            encoder.writeObject(nextEmpresaId);
            encoder.writeObject(nextProdutoId);
            encoder.writeObject(nextPedidoNumero);
            encoder.writeObject(nextEntregaId);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE_NAME)))) {
            usuarios = (List<Usuario>) decoder.readObject();
            empresas = (List<Empresa>) decoder.readObject();
            produtos = (List<Produto>) decoder.readObject();
            pedidos = (List<Pedido>) decoder.readObject();
            entregas = (List<Entrega>) decoder.readObject();
            nextUsuarioId = (int) decoder.readObject();
            nextEmpresaId = (int) decoder.readObject();
            nextProdutoId = (int) decoder.readObject();
            nextPedidoNumero = (int) decoder.readObject();
            nextEntregaId = (int) decoder.readObject();
        } catch (IOException e) { e.printStackTrace(); }
    }
}