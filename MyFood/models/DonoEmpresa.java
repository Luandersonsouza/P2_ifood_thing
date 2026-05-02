// DonoEmpresa.java
package MyFood.models;

public class DonoEmpresa extends Usuario {
    private String cpf;

    public DonoEmpresa() {}

    public DonoEmpresa(String nome, String email, String senha, String endereco, String cpf) {
        super(nome, email, senha, endereco);
        this.cpf = cpf;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    @Override
    public String getTipo() {
        return "donoEmpresa";
    }
}