package models;

import java.util.Date;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class Consulta extends Model {
    
    @Temporal(TemporalType.DATE)
    public Date dataConsulta;
    
    @ManyToOne
    public Cliente cliente;
}