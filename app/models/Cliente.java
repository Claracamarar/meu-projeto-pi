package models;

import java.util.List;
import javax.persistence.*;
import play.db.jpa.Model;
import play.db.jpa.Blob;

@Entity
public class Cliente extends Model {
    public String nomeCompleto;
    public String cpf;
    public String telefone;
    public String email;
    
    @Enumerated(EnumType.STRING)
    public Status status = Status.ATIVO;
    
    // Campo para armazenar a foto de perfil
    public Blob foto;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    public List<Consulta> consultas;
    
    
    // Verifica se o CPF já está cadastrado no sistema
     
    public static boolean cpfExiste(String cpf, Long id) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        
        // Remove formatação do CPF para comparação
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        Cliente existente = Cliente.find(
            "cpf = ?1 and id <> ?2 and status = ?3", 
            cpfLimpo, 
            id != null ? id : 0L,
            Status.ATIVO
        ).first();
        
        return existente != null;
    }
    
    
    //  Valida o CPF usando o algoritmo oficial
     
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        
        // Remove formatação
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        try {
            // Calcula o primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;
            
            // Verifica o primeiro dígito
            if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigito) {
                return false;
            }
            
            // Calcula o segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;
            
            // Verifica o segundo dígito
            return Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return nomeCompleto;
    }
}