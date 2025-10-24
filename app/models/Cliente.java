package models;

import java.util.List;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class Cliente extends Model {
    public String nomeCompleto;
    public String telefone;
    public String email;
    
    @Enumerated(EnumType.STRING)
    public Status status = Status.ATIVO;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    public List<Consulta> consultas;
    
    @Override
    public String toString() {
        return nomeCompleto;
    }
}