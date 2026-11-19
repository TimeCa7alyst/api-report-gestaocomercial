package br.com.gestaocomercial.report.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.SQLInsert;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;

@Entity
@Table(name = "cliente_report")
@SQLInsert(sql = """
    INSERT INTO cliente_report (cidade, data_cadastro, email, estado, nome, status_cliente, tipo, id, ultima_atualizacao)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(6))
    ON DUPLICATE KEY UPDATE
        cidade = VALUES(cidade),
        email = VALUES(email),
        estado = VALUES(estado),
        nome = VALUES(nome),
        status_cliente = VALUES(status_cliente),
        tipo = VALUES(tipo),
        ultima_atualizacao = NOW(6)
    """)
public class ClienteReport implements Persistable<Integer> {
    @Id
    private Integer id;
    private String nome;
    private String tipo;
    private LocalDate dataCadastro;
    private String statusCliente;
    private String cidade;
    private String estado;
    private String email;

    public ClienteReport() {}

    public ClienteReport(Integer id, String nome, String tipo, LocalDate dataCadastro,
                         String statusCliente, String cidade, String estado, String email) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.statusCliente = statusCliente;
        this.cidade = cidade;
        this.estado = estado;
        this.email = email;
    }

    @Override
    @Transient
    public boolean isNew() {
        return true;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getStatusCliente() {
        return statusCliente;
    }

    public void setStatusCliente(String statusCliente) {
        this.statusCliente = statusCliente;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}