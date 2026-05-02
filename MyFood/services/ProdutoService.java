// ProdutoService.java
package MyFood.services;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoService {
    private Database db = Database.getInstance();
    private EmpresaService empresaService = new EmpresaService();

    public int criarProduto(int idEmpresa, String nome, float valor, String categoria) {
        // Verifica se empresa existe
        empresaService.getAtributoEmpresa(idEmpresa, "nome");

        Validador.validarNome(nome);
        Validador.validarValor(valor);
        Validador.validarCategoria(categoria);

        // Produto com mesmo nome na mesma empresa
        boolean existe = db.getProdutos().stream()
                .anyMatch(p -> p.getIdEmpresa() == idEmpresa && p.getNome().equals(nome));
        if (existe) {
            throw new MyFoodException("Ja existe um produto com esse nome para essa empresa");
        }

        Produto p = new Produto(idEmpresa, nome, valor, categoria);
        db.addProduto(p);
        return p.getId();
    }

    public void editarProduto(int idProduto, String nome, float valor, String categoria) {
        Produto produto = db.getProdutos().stream()
                .filter(p -> p.getId() == idProduto)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Produto nao cadastrado"));

        Validador.validarNome(nome);
        Validador.validarValor(valor);
        Validador.validarCategoria(categoria);

        // Verificar se novo nome conflita com outro produto da mesma empresa (exceto ele mesmo)
        boolean conflito = db.getProdutos().stream()
                .anyMatch(p -> p.getIdEmpresa() == produto.getIdEmpresa() &&
                        p.getNome().equals(nome) &&
                        p.getId() != idProduto);
        if (conflito) {
            throw new MyFoodException("Ja existe um produto com esse nome para essa empresa");
        }

        produto.setNome(nome);
        produto.setValor(valor);
        produto.setCategoria(categoria);
        db.updateProduto(produto);
    }

    public String getProduto(String nome, int idEmpresa, String atributo) {
        Produto produto = db.getProdutos().stream()
                .filter(p -> p.getIdEmpresa() == idEmpresa && p.getNome().equals(nome))
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Produto nao encontrado"));

        switch (atributo.toLowerCase()) {
            case "valor": return String.format(Locale.US, "%.2f", produto.getValor());
            case "categoria": return produto.getCategoria();
            case "empresa":
                Empresa e = db.getEmpresas().stream()
                        .filter(em -> em.getId() == idEmpresa)
                        .findFirst().orElse(null);
                return e != null ? e.getNome() : "";
            default:
                throw new MyFoodException("Atributo nao existe");
        }
    }

    public String listarProdutos(int idEmpresa) {
        boolean empresaExiste = db.getEmpresas().stream().anyMatch(e -> e.getId() == idEmpresa);
        if (!empresaExiste) {
            throw new MyFoodException("Empresa nao encontrada");
        }
        List<Produto> produtos = db.getProdutos().stream()
                .filter(p -> p.getIdEmpresa() == idEmpresa)
                .collect(Collectors.toList());

        if (produtos.isEmpty()) {
            return "{[]}";
        }
        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < produtos.size(); i++) {
            sb.append(produtos.get(i).getNome());
            if (i < produtos.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public Produto getProdutoById(int id) {
        return db.getProdutos().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Produto nao encontrado"));
    }
}
