package models;

import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class Usuario extends Model {
    
    public String nomeCompleto;
    public String email;
    public String login;
    public String senha;
    
    @Enumerated(EnumType.STRING)
    public Status status;
    
    @Enumerated(EnumType.STRING)
    public Perfil perfil;
    
    public Usuario() {
        this.status = Status.ATIVO;
        this.perfil = Perfil.FUNCIONARIO; // Perfil padrão agora é FUNCIONARIO
    }
    
    // Método de autenticação
    public static Usuario autenticar(String login, String senha) {
        return Usuario.find("login = ?1 and senha = ?2 and status = ?3", 
                           login, senha, Status.ATIVO).first();
    }
    
    // Verifica se login já existe
    public static boolean loginExiste(String login, Long id) {
        Usuario existente = Usuario.find("login = ?1 and id <> ?2", login, id != null ? id : 0L).first();
        return existente != null;
    }
    
    @Override
    public String toString() {
        return nomeCompleto;
    }
}