package models;

import java.util.Date;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class SolicitacaoAcesso extends Model {
    
    @ManyToOne
    public Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    public StatusSolicitacao status;
    
    @Enumerated(EnumType.STRING)
    public TipoAcesso tipoAcesso; // PERMANENTE ou UNICO
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date dataSolicitacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date dataResposta;
    
    public Long administradorId; // ID do admin que aprovou/rejeitou
    
    public boolean acessoUtilizado; // Para controlar acesso único
    
    public SolicitacaoAcesso() {
        this.dataSolicitacao = new Date();
        this.status = StatusSolicitacao.PENDENTE;
        this.acessoUtilizado = false;
    }
}