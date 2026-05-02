// Restaurante.java
package MyFood.models;

public class Restaurante extends Empresa {
    private String tipoCozinha;

    public Restaurante() {}

    public Restaurante(String nome, String endereco, int idDono, String tipoCozinha) {
        super(nome, endereco, idDono);
        this.tipoCozinha = tipoCozinha;
    }

    public String getTipoCozinha() { return tipoCozinha; }
    public void setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }

    @Override
    public String getTipo() {
        return "restaurante";
    }
}