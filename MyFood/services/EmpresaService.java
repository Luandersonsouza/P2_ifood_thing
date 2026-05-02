package MyFood.services;

import MyFood.exceptions.MyFoodException;
import MyFood.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmpresaService {
    private Database db = Database.getInstance();
    private UsuarioService usuarioService = new UsuarioService();

    public int criarRestaurante(int idDono, String nome, String endereco, String tipoCozinha) {
        Usuario dono = usuarioService.getUsuario(idDono);
        if (!(dono instanceof DonoEmpresa)) {
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        }

        Validador.validarNome(nome);
        if (endereco == null || endereco.trim().isEmpty()) throw new MyFoodException("Endereco da empresa invalido");
        if (tipoCozinha == null || tipoCozinha.trim().isEmpty())
            throw new IllegalArgumentException("Tipo de cozinha invalido");

        // Verifica se o mesmo dono já tem empresa com mesmo nome
        List<Empresa> empresasDoDonoComMesmoNome = db.getEmpresas().stream()
                .filter(e -> e.getIdDono() == idDono && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (!empresasDoDonoComMesmoNome.isEmpty()) {
            boolean mesmoEndereco = empresasDoDonoComMesmoNome.stream()
                    .anyMatch(e -> e.getEndereco().equals(endereco));
            if (mesmoEndereco) {
                throw new MyFoodException("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        } else {
            boolean nomeExiste = db.getEmpresas().stream().anyMatch(e -> e.getNome().equals(nome));
            if (nomeExiste) {
                throw new MyFoodException("Empresa com esse nome ja existe");
            }
        }

        Restaurante r = new Restaurante(nome, endereco, idDono, tipoCozinha);
        db.addEmpresa(r);
        return r.getId();
    }

    public int criarMercado(int idDono, String nome, String endereco, String abre, String fecha, String tipoMercado) {
        Usuario dono = usuarioService.getUsuario(idDono);
        if (!(dono instanceof DonoEmpresa)) {
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        }

        Validador.validarNome(nome);
        if (endereco == null || endereco.trim().isEmpty()) throw new MyFoodException("Endereco da empresa invalido");
        if (tipoMercado == null || tipoMercado.trim().isEmpty()) throw new MyFoodException("Tipo de mercado invalido");

        if (abre == null || fecha == null || abre.isBlank() || fecha.isBlank()) {
            throw new MyFoodException("Horario invalido");
        }
        Validador.validarHorario(abre, fecha);

        List<Empresa> empresasDoDonoComMesmoNome = db.getEmpresas().stream()
                .filter(e -> e.getIdDono() == idDono && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (!empresasDoDonoComMesmoNome.isEmpty()) {
            boolean mesmoEndereco = empresasDoDonoComMesmoNome.stream()
                    .anyMatch(e -> e.getEndereco().equals(endereco));
            if (mesmoEndereco) {
                throw new MyFoodException("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        } else {
            boolean nomeExiste = db.getEmpresas().stream().anyMatch(e -> e.getNome().equals(nome));
            if (nomeExiste) {
                throw new MyFoodException("Empresa com esse nome ja existe");
            }
        }

        Mercado m = new Mercado(nome, endereco, idDono, abre, fecha, tipoMercado);
        db.addEmpresa(m);
        return m.getId();
    }

    public int criarFarmacia(int idDono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        Usuario dono = usuarioService.getUsuario(idDono);
        if (!(dono instanceof DonoEmpresa)) {
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        }

        Validador.validarNome(nome);
        if (endereco == null || endereco.trim().isEmpty()) throw new MyFoodException("Endereco da empresa invalido");
        if (numeroFuncionarios < 0) throw new MyFoodException("Numero de funcionarios invalido");

        List<Empresa> empresasDoDonoComMesmoNome = db.getEmpresas().stream()
                .filter(e -> e.getIdDono() == idDono && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (!empresasDoDonoComMesmoNome.isEmpty()) {
            boolean mesmoEndereco = empresasDoDonoComMesmoNome.stream()
                    .anyMatch(e -> e.getEndereco().equals(endereco));
            if (mesmoEndereco) {
                throw new MyFoodException("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        } else {
            boolean nomeExiste = db.getEmpresas().stream().anyMatch(e -> e.getNome().equals(nome));
            if (nomeExiste) {
                throw new MyFoodException("Empresa com esse nome ja existe");
            }
        }

        Farmacia f = new Farmacia(nome, endereco, idDono, aberto24Horas, numeroFuncionarios);
        db.addEmpresa(f);
        return f.getId();
    }

    public String getEmpresasDoUsuario(int idDono) {
        Usuario usuario = usuarioService.getUsuario(idDono);
        if (!(usuario instanceof DonoEmpresa)) {
            throw new MyFoodException("Usuario nao pode criar uma empresa");
        }
        List<Empresa> empresas = db.getEmpresas().stream()
                .filter(e -> e.getIdDono() == idDono)
                .collect(Collectors.toList());

        if (empresas.isEmpty()) {
            return "{[]}";
        }

        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);
            sb.append("[").append(e.getNome()).append(", ").append(e.getEndereco()).append("]");
            if (i < empresas.size() - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

    public String getAtributoEmpresa(int idEmpresa, String atributo) {
        Empresa empresa = db.getEmpresas().stream()
                .filter(e -> e.getId() == idEmpresa)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Empresa nao cadastrada"));

        switch (atributo.toLowerCase()) {
            case "nome": return empresa.getNome();
            case "endereco": return empresa.getEndereco();
            case "dono":
                Usuario dono = usuarioService.getUsuario(empresa.getIdDono());
                return dono.getNome();
            case "tipocozinha":
                if (empresa instanceof Restaurante) return ((Restaurante) empresa).getTipoCozinha();
                throw new MyFoodException("Atributo invalido");
            case "abre":
                if (empresa instanceof Mercado) return ((Mercado) empresa).getAbre();
                throw new MyFoodException("Atributo invalido");
            case "fecha":
                if (empresa instanceof Mercado) return ((Mercado) empresa).getFecha();
                throw new MyFoodException("Atributo invalido");
            case "tipomercado":
                if (empresa instanceof Mercado) return ((Mercado) empresa).getTipoMercado();
                throw new MyFoodException("Atributo invalido");
            case "aberto24horas":
                if (empresa instanceof Farmacia) return String.valueOf(((Farmacia) empresa).isAberto24Horas());
                throw new MyFoodException("Atributo invalido");
            case "numerofuncionarios":
                if (empresa instanceof Farmacia) return String.valueOf(((Farmacia) empresa).getNumeroFuncionarios());
                throw new MyFoodException("Atributo invalido");
            default:
                throw new MyFoodException("Atributo invalido");
        }
    }

    public int getIdEmpresa(int idDono, String nome, int indice) {
        Validador.validarNome(nome);
        if (indice < 0) throw new MyFoodException("Indice invalido");

        List<Empresa> empresasDoDono = db.getEmpresas().stream()
                .filter(e -> e.getIdDono() == idDono && e.getNome().equals(nome))
                .collect(Collectors.toList());

        if (empresasDoDono.isEmpty()) throw new MyFoodException("Nao existe empresa com esse nome");
        if (indice >= empresasDoDono.size()) throw new MyFoodException("Indice maior que o esperado");
        return empresasDoDono.get(indice).getId();
    }

    public void cadastrarEntregador(int idEmpresa, int idEntregador) {
        Empresa empresa = getEmpresa(idEmpresa);
        Usuario entregador = usuarioService.getUsuario(idEntregador);
        if (!(entregador instanceof Entregador)) throw new MyFoodException("Usuario nao e um entregador");
        if (!empresa.getEntregadores().contains(idEntregador)) {
            empresa.getEntregadores().add(idEntregador);
            db.updateEmpresa(empresa);
        }
    }

    public List<String> getEntregadores(int idEmpresa) {
        Empresa empresa = getEmpresa(idEmpresa);
        List<String> emails = new ArrayList<>();
        for (int id : empresa.getEntregadores()) {
            Usuario u = db.getUsuarios().stream().filter(us -> us.getId() == id).findFirst().orElse(null);
            if (u != null) emails.add(u.getEmail());
        }
        return emails;
    }

    public List<Empresa> getEmpresasDoEntregador(int idEntregador) {
        return db.getEmpresas().stream()
                .filter(e -> e.getEntregadores().contains(idEntregador))
                .collect(Collectors.toList());
    }

    public void alterarFuncionamento(int idMercado, String abre, String fecha) {
        Empresa empresa = getEmpresa(idMercado);
        if (!(empresa instanceof Mercado)) throw new MyFoodException("Nao e um mercado valido");
        if (abre == null || fecha == null || abre.isBlank() || fecha.isBlank()) throw new MyFoodException("Horario invalido");
        Validador.validarHorario(abre, fecha);
        Mercado m = (Mercado) empresa;
        m.setAbre(abre);
        m.setFecha(fecha);
        db.updateEmpresa(m);
    }

    private Empresa getEmpresa(int id) {
        return db.getEmpresas().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new MyFoodException("Empresa nao cadastrada"));
    }
}