package tests;

import MyFood.Facade;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class TestRunner {
    private final Facade facade = new Facade();
    private final Map<String, String> variables = new HashMap<>();

    private static final Set<String> COMMANDS = new HashSet<>(Arrays.asList(
        "zerarSistema", "criarUsuario", "login", "getAtributoUsuario", "criarEmpresa",
        "getEmpresasDoUsuario", "getAtributoEmpresa", "getIdEmpresa", "criarProduto",
        "editarProduto", "getProduto", "listarProdutos", "criarPedido", "adicionarProduto",
        "getPedidos", "fecharPedido", "removerProduto", "getNumeroPedido", "liberarPedido",
        "obterPedido", "criarEntrega", "getEntrega", "getIdEntrega", "entregar",
        "alterarFuncionamento", "cadastrarEntregador", "getEntregadores", "getEmpresas",
        "encerrarSistema"
    ));

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java tests.TestRunner <arquivo...>");
            return;
        }
        TestRunner runner = new TestRunner();
        for (String file : args) {
            runner.runFile(file);
        }
        System.out.println("\nTodos os testes concluídos.");
    }

    public void runFile(String filename) throws IOException {
        System.out.println("=== " + filename + " ===");
        List<String> lines = Files.readAllLines(Path.of(filename));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            try {
                processLine(line);
            } catch (Throwable t) {
                System.err.println("Falha na linha " + (i+1) + ": " + line);
                t.printStackTrace(System.err);
                throw new RuntimeException("Teste interrompido");
            }
        }
    }

    private void processLine(String line) throws Exception {
    line = replaceVariables(line);

    // Testa primeiro os comandos de verificação
    if (line.startsWith("expectError ")) {
        String rest = line.substring("expectError ".length()).trim();
        Pattern p = Pattern.compile("\"([^\"]*)\"\\s+(.*)");
        Matcher m = p.matcher(rest);
        if (!m.find()) throw new RuntimeException("Sintaxe inválida: " + line);
        String expectedMsg = m.group(1);
        String command = m.group(2);
        try {
            executeCommand(command);
            throw new RuntimeException("ERRO: esperava exceção mas não foi lançada.");
        } catch (RuntimeException e) {
            if (!expectedMsg.equals(e.getMessage())) {
                System.err.println("   ERRO: mensagem incorreta da exceção.");
                System.err.println("   Esperado: " + expectedMsg);
                System.err.println("   Obtido:   " + e.getMessage());
                throw new RuntimeException("Falha no expectError");
            }
            System.out.println("   OK: exceção correta \"" + e.getMessage() + "\"");
        }
        return;
    }

    if (line.startsWith("expect ")) {
        String rest = line.substring("expect ".length()).trim();
        Pattern p = Pattern.compile("(?:\"([^\"]*)\"|(\\S+))\\s+(.*)");
        Matcher m = p.matcher(rest);
        if (!m.find()) throw new RuntimeException("Sintaxe inválida: " + line);
        String expectedValue = m.group(1) != null ? m.group(1) : m.group(2);
        String command = m.group(3);
        Object result = executeCommand(command);
        String actualValue = (result == null) ? "null" : result.toString();
        if (!expectedValue.equals(actualValue)) {
            System.err.println("   ERRO: valor incorreto.");
            System.err.println("   Comando: " + command);
            System.err.println("   Esperado: " + expectedValue);
            System.err.println("   Obtido:   " + actualValue);
            throw new RuntimeException("Falha no expect");
        }
        System.out.println("   OK: valor \"" + actualValue + "\"");
        return;
    }

    if (line.contains("=")) {
        String firstToken = line.split("\\s+")[0];
        if (!COMMANDS.contains(firstToken)) {
            // É uma atribuição: var = comando
            int idx = line.indexOf('=');
            String var = line.substring(0, idx).trim();
            String command = line.substring(idx + 1).trim();
            Object result = executeCommand(command);
            variables.put(var, result != null ? result.toString() : "null");
            return;
        }
    }

    // Qualquer outra linha é um comando sem retorno (ex: fecharPedido)
    if (line.equals("encerrarSistema") || line.equals("quit")) {
        return;
    }
    executeCommand(line);
}

    private Object executeCommand(String command) throws Exception {
        command = command.trim();

        if (command.startsWith("zerarSistema")) {
            facade.zerarSistema();
            return null;
        }

        else if (command.startsWith("criarUsuario ")) {
            Map<String, String> p = extractParams(command.substring("criarUsuario ".length()));
            if (p.containsKey("cpf")) {
                facade.criarUsuario(p.get("nome"), p.get("email"), p.get("senha"), p.get("endereco"), p.get("cpf"));
            } else if (p.containsKey("veiculo") && p.containsKey("placa")) {
                facade.criarUsuario(p.get("nome"), p.get("email"), p.get("senha"), p.get("endereco"), p.get("veiculo"), p.get("placa"));
            } else {
                facade.criarUsuario(p.get("nome"), p.get("email"), p.get("senha"), p.get("endereco"));
            }
            return null;
        }

        else if (command.startsWith("login ")) {
            Map<String, String> p = extractParams(command.substring("login ".length()));
            return facade.login(p.get("email"), p.get("senha"));
        }

        else if (command.startsWith("getAtributoUsuario ")) {
            Map<String, String> p = extractParams(command.substring("getAtributoUsuario ".length()));
            return facade.getAtributoUsuario(Integer.parseInt(p.get("id")), p.get("atributo"));
        }

        else if (command.startsWith("criarEmpresa ")) {
            Map<String, String> p = extractParams(command.substring("criarEmpresa ".length()));
            String tipo = p.get("tipoEmpresa");
            int dono = Integer.parseInt(p.get("dono"));
            String nome = p.get("nome");
            String endereco = p.get("endereco");
            if ("restaurante".equals(tipo)) {
                return facade.criarEmpresa(tipo, dono, nome, endereco, p.get("tipoCozinha"));
            } else if ("mercado".equals(tipo)) {
                return facade.criarEmpresa(tipo, dono, nome, endereco, p.get("abre"), p.get("fecha"), p.get("tipoMercado"));
            } else if ("farmacia".equals(tipo)) {
                boolean a24 = p.get("aberto24Horas").equalsIgnoreCase("true");
                int func = Integer.parseInt(p.get("numeroFuncionarios"));
                return facade.criarEmpresa(tipo, dono, nome, endereco, a24, func);
            }
            if (p.containsKey("abre") || p.containsKey("fecha") || p.containsKey("tipoMercado")) {
                return facade.criarEmpresa(tipo, dono, nome, endereco, p.get("abre"), p.get("fecha"), p.get("tipoMercado"));
            }
            if (p.containsKey("tipoCozinha")) {
                return facade.criarEmpresa(tipo, dono, nome, endereco, p.get("tipoCozinha"));
            }
            throw new RuntimeException("Tipo de empresa invalido");
        }

        else if (command.startsWith("getEmpresasDoUsuario ")) {
            Map<String, String> p = extractParams(command.substring("getEmpresasDoUsuario ".length()));
            return facade.getEmpresasDoUsuario(Integer.parseInt(p.get("idDono")));
        }

        else if (command.startsWith("getAtributoEmpresa ")) {
            Map<String, String> p = extractParams(command.substring("getAtributoEmpresa ".length()));
            return facade.getAtributoEmpresa(Integer.parseInt(p.get("empresa")), p.get("atributo"));
        }

        else if (command.startsWith("getIdEmpresa ")) {
            Map<String, String> p = extractParams(command.substring("getIdEmpresa ".length()));
            return facade.getIdEmpresa(Integer.parseInt(p.get("idDono")), p.get("nome"), Integer.parseInt(p.get("indice")));
        }

        else if (command.startsWith("criarProduto ")) {
            Map<String, String> p = extractParams(command.substring("criarProduto ".length()));
            return facade.criarProduto(Integer.parseInt(p.get("empresa")), p.get("nome"), Float.parseFloat(p.get("valor")), p.get("categoria"));
        }

        else if (command.startsWith("editarProduto ")) {
            Map<String, String> p = extractParams(command.substring("editarProduto ".length()));
            facade.editarProduto(Integer.parseInt(p.get("produto")), p.get("nome"), Float.parseFloat(p.get("valor")), p.get("categoria"));
            return null;
        }

        else if (command.startsWith("getProduto ")) {
            Map<String, String> p = extractParams(command.substring("getProduto ".length()));
            return facade.getProduto(p.get("nome"), Integer.parseInt(p.get("empresa")), p.get("atributo"));
        }

        else if (command.startsWith("listarProdutos ")) {
            Map<String, String> p = extractParams(command.substring("listarProdutos ".length()));
            return facade.listarProdutos(Integer.parseInt(p.get("empresa")));
        }

        else if (command.startsWith("criarPedido ")) {
            Map<String, String> p = extractParams(command.substring("criarPedido ".length()));
            return facade.criarPedido(Integer.parseInt(p.get("cliente")), Integer.parseInt(p.get("empresa")));
        }

        else if (command.startsWith("adicionarProduto ")) {
            Map<String, String> p = extractParams(command.substring("adicionarProduto ".length()));
            String numero = p.containsKey("numero") ? p.get("numero") : p.get("pedido");
            facade.adicionarProduto(Integer.parseInt(numero), Integer.parseInt(p.get("produto")));
            return null;
        }

        else if (command.startsWith("getPedidos ")) {
            Map<String, String> p = extractParams(command.substring("getPedidos ".length()));
            return facade.getPedidos(Integer.parseInt(p.get("pedido")), p.get("atributo"));
        }

        else if (command.startsWith("fecharPedido ")) {
            Map<String, String> p = extractParams(command.substring("fecharPedido ".length()));
            facade.fecharPedido(Integer.parseInt(p.get("numero")));
            return null;
        }

        else if (command.startsWith("removerProduto ")) {
            Map<String, String> p = extractParams(command.substring("removerProduto ".length()));
            facade.removerProduto(Integer.parseInt(p.get("pedido")), p.get("produto"));
            return null;
        }

        else if (command.startsWith("getNumeroPedido ")) {
            Map<String, String> p = extractParams(command.substring("getNumeroPedido ".length()));
            return facade.getNumeroPedido(Integer.parseInt(p.get("cliente")), Integer.parseInt(p.get("empresa")), Integer.parseInt(p.get("indice")));
        }

        else if (command.startsWith("liberarPedido ")) {
            Map<String, String> p = extractParams(command.substring("liberarPedido ".length()));
            facade.liberarPedido(Integer.parseInt(p.get("numero")));
            return null;
        }

        else if (command.startsWith("obterPedido ")) {
            Map<String, String> p = extractParams(command.substring("obterPedido ".length()));
            return facade.obterPedido(Integer.parseInt(p.get("entregador")));
        }

        else if (command.startsWith("criarEntrega ")) {
            Map<String, String> p = extractParams(command.substring("criarEntrega ".length()));
            return facade.criarEntrega(Integer.parseInt(p.get("pedido")), Integer.parseInt(p.get("entregador")), p.get("destino"));
        }

        else if (command.startsWith("getEntrega ")) {
            Map<String, String> p = extractParams(command.substring("getEntrega ".length()));
            return facade.getEntrega(Integer.parseInt(p.get("id")), p.get("atributo"));
        }

        else if (command.startsWith("getIdEntrega ")) {
            Map<String, String> p = extractParams(command.substring("getIdEntrega ".length()));
            return facade.getIdEntrega(Integer.parseInt(p.get("pedido")));
        }

        else if (command.startsWith("entregar ")) {
            Map<String, String> p = extractParams(command.substring("entregar ".length()));
            facade.entregar(Integer.parseInt(p.get("entrega")));
            return null;
        }

        else if (command.startsWith("alterarFuncionamento ")) {
            Map<String, String> p = extractParams(command.substring("alterarFuncionamento ".length()));
            facade.alterarFuncionamento(Integer.parseInt(p.get("mercado")), p.get("abre"), p.get("fecha"));
            return null;
        }

        else if (command.startsWith("cadastrarEntregador ")) {
            Map<String, String> p = extractParams(command.substring("cadastrarEntregador ".length()));
            facade.cadastrarEntregador(Integer.parseInt(p.get("empresa")), Integer.parseInt(p.get("entregador")));
            return null;
        }

        else if (command.startsWith("getEntregadores ")) {
            Map<String, String> p = extractParams(command.substring("getEntregadores ".length()));
            return facade.getEntregadores(Integer.parseInt(p.get("empresa")));
        }

        else if (command.startsWith("getEmpresas ")) {
            Map<String, String> p = extractParams(command.substring("getEmpresas ".length()));
            return facade.getEmpresas(Integer.parseInt(p.get("entregador")));
        }

        else {
            throw new RuntimeException("Comando não reconhecido: " + command);
        }
    }

    private String replaceVariables(String line) {
        Pattern p = Pattern.compile("\\$\\{(\\w+)\\}");
        Matcher m = p.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String varName = m.group(1);
            String value = variables.get(varName);
            if (value == null) throw new RuntimeException("Variável não definida: " + varName);
            m.appendReplacement(sb, value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private Map<String, String> extractParams(String paramStr) {
        Map<String, String> map = new HashMap<>();
        Pattern p = Pattern.compile("(\\w+)=(?:\"([^\"]*)\"|([^\\s\"]*))");
        Matcher m = p.matcher(paramStr);
        while (m.find()) {
            String key = m.group(1);
            String val = m.group(2) != null ? m.group(2) : m.group(3);
            if (val != null && val.isEmpty() && m.group(2) == null) val = null;
            map.put(key, val);
        }
        return map;
    }
}
