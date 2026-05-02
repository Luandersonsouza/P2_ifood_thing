package MyFood.services;

import java.util.List;
import MyFood.models.Entregador;

public class Validador {
    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome invalido");
    }
    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Email invalido");
    }
    public static void validarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("Senha invalido");
    }
    public static void validarEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) throw new IllegalArgumentException("Endereco invalido");
    }
    public static void validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 14 || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"))
            throw new IllegalArgumentException("CPF invalido");
    }
    public static void validarValor(float valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor invalido");
    }
    public static void validarCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) throw new IllegalArgumentException("Categoria invalido");
    }
    public static void validarHora(String hora) {
        if (hora == null || !hora.matches("\\d{2}:\\d{2}")) throw new IllegalArgumentException("Formato de hora invalido");
        String[] p = hora.split(":");
        int h = Integer.parseInt(p[0]); int m = Integer.parseInt(p[1]);
        if (h < 0 || h > 23 || m < 0 || m > 59) throw new IllegalArgumentException("Formato de hora invalido");
    }
    public static void validarHorario(String abre, String fecha) {
        validarHora(abre); validarHora(fecha);
        int minAbre = toMinutes(abre); int minFecha = toMinutes(fecha);
        if (minAbre >= minFecha) throw new IllegalArgumentException("Horario invalido");
    }
    private static int toMinutes(String hora) {
        String[] p = hora.split(":"); return Integer.parseInt(p[0])*60 + Integer.parseInt(p[1]);
    }
    public static void validarPlacaUnica(String placa, List<Entregador> entregadores) {
        for (Entregador e : entregadores) {
            if (e.getPlaca().equals(placa)) throw new IllegalArgumentException("Placa invalido");
        }
    }
}