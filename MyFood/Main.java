package MyFood;

import MyFood.services.*;
import MyFood.models.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== MyFood System ===");
        Facade facade = new Facade();

        // Demonstração de uso básico
        try {
            facade.zerarSistema();

            facade.criarUsuario("Maria Cliente", "maria@email.com", "123", "Rua das Rosas, 10");
            int idMaria = facade.login("maria@email.com", "123");
            System.out.println("Maria logada, id=" + idMaria);

            facade.criarUsuario("Joao Dono", "joao@email.com", "456", "Av. Central, 100", "123.456.789-00");
            int idJoao = facade.login("joao@email.com", "456");
            System.out.println("Joao (dono) logado, id=" + idJoao);

            int idRest = facade.criarEmpresa("restaurante", idJoao, "Restaurante do Joao", "Av. Principal, 1", "Italiana");
            System.out.println("Restaurante criado, id=" + idRest);

            int idProd = facade.criarProduto(idRest, "Pizza Margherita", 35.0f, "alimento");
            System.out.println("Produto criado, id=" + idProd);

            int idPedido = facade.criarPedido(idMaria, idRest);
            facade.adicionarProduto(idPedido, idProd);
            System.out.println("Pedido criado e produto adicionado. Pedido #" + idPedido);

            String estado = facade.getPedidos(idPedido, "estado");
            System.out.println("Estado do pedido: " + estado);

            System.out.println("Sistema funcionando corretamente!");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}