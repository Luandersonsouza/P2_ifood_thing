// Entregador.java
package MyFood.models;

public class Entregador extends Usuario {
    private String veiculo;
    private String placa;

    public Entregador() {}

    public Entregador(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        super(nome, email, senha, endereco);
        this.veiculo = veiculo;
        this.placa = placa;
    }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    @Override
    public String getTipo() {
        return "entregador";
    }
}