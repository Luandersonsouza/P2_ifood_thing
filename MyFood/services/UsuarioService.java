package MyFood.services;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;


import java.util.List;

public class UsuarioService {
    private Database db = Database.getInstance();

    public void criarCliente(String nome, String email, String senha, String endereco) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);
        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }
        Cliente cliente = new Cliente(nome, email, senha, endereco);
        db.addUsuario(cliente);
    }

    public void criarDonoEmpresa(String nome, String email, String senha, String endereco, String cpf) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);
        Validador.validarCpf(cpf);
        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }
        DonoEmpresa dono = new DonoEmpresa(nome, email, senha, endereco, cpf);
        db.addUsuario(dono);
    }

    public void criarEntregador(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        Validador.validarNome(nome);
        Validador.validarEmail(email);
        Validador.validarSenha(senha);
        Validador.validarEndereco(endereco);
        if (veiculo == null || veiculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Veiculo invalido");
        }
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa invalido");
        }
        // Unicidade de placa
        for (Usuario u : db.getUsuarios()) {
            if (u instanceof Entregador && ((Entregador) u).getPlaca().equals(placa)) {
                throw new MyFoodException("Placa invalido");
            }
        }
        if (db.getUsuarios().stream().anyMatch(u -> u.getEmail().equals(email))) {
            throw new MyFoodException("Conta com esse email ja existe");
        }
        Entregador entregador = new Entregador(nome, email, senha, endereco, veiculo, placa);
        db.addUsuario(entregador);
    }

    public int login(String email, String senha) {
        Usuario usuario = db.getUsuarios().stream()
                .filter(u -> u.getEmail().equals(email) && u.getSenha().equals(senha))
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Login ou senha invalidos"));
        return usuario.getId();
    }

    public String getAtributoUsuario(int id, String atributo) {
        Usuario usuario = db.getUsuarios().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Usuario nao cadastrado."));

        if (atributo == null || atributo.trim().isEmpty()) {
            throw new MyFoodException("Atributo invalido");
        }

        switch (atributo.toLowerCase()) {
            case "nome": return usuario.getNome();
            case "email": return usuario.getEmail();
            case "senha": return usuario.getSenha();
            case "endereco": return usuario.getEndereco();
            case "cpf":
                if (usuario instanceof DonoEmpresa) return ((DonoEmpresa) usuario).getCpf();
                throw new MyFoodException("Atributo invalido");
            case "veiculo":
                if (usuario instanceof Entregador) return ((Entregador) usuario).getVeiculo();
                throw new MyFoodException("Atributo invalido");
            case "placa":
                if (usuario instanceof Entregador) return ((Entregador) usuario).getPlaca();
                throw new MyFoodException("Atributo invalido");
            default:
                throw new MyFoodException("Atributo invalido");
        }
    }

    public Usuario getUsuario(int id) {
        return db.getUsuarios().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Usuario nao cadastrado."));
    }
}
