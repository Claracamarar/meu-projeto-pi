package controllers;

import Annotations.Administrador;
import java.util.Date;
import java.util.List;
import models.SolicitacaoAcesso;
import models.StatusSolicitacao;
import models.TipoAcesso;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Solicitacoes extends Controller {
    
    /**
     * Lista todas as solicitações pendentes - apenas ADMINISTRADOR
     */
    @Administrador
    public static void listarPendentes() {
        List<SolicitacaoAcesso> solicitacoes = SolicitacaoAcesso.find(
            "status = ?1 order by dataSolicitacao desc", 
            StatusSolicitacao.PENDENTE
        ).fetch();
        render(solicitacoes);
    }
    
    /**
     * Lista todas as solicitações (histórico) - apenas ADMINISTRADOR
     */
    @Administrador
    public static void listarTodas() {
        List<SolicitacaoAcesso> solicitacoes = SolicitacaoAcesso.find(
            "order by dataSolicitacao desc"
        ).fetch();
        render(solicitacoes);
    }
    
    /**
     * Aprova uma solicitação - apenas ADMINISTRADOR
     */
    @Administrador
    public static void aprovar(Long id, String tipoAcesso) {
        SolicitacaoAcesso solicitacao = SolicitacaoAcesso.findById(id);
        
        if (solicitacao == null) {
            flash.error("Solicitação não encontrada!");
            listarPendentes();
            return;
        }
        
        if (tipoAcesso == null || tipoAcesso.trim().isEmpty()) {
            flash.error("Selecione o tipo de acesso!");
            listarPendentes();
            return;
        }
        
        try {
            solicitacao.status = StatusSolicitacao.APROVADO;
            solicitacao.tipoAcesso = TipoAcesso.valueOf(tipoAcesso);
            solicitacao.dataResposta = new Date();
            solicitacao.administradorId = Long.parseLong(session.get("usuarioId"));
            solicitacao.save();
            
            String tipoMsg = tipoAcesso.equals("PERMANENTE") ? "permanente" : "único";
            flash.success("Acesso " + tipoMsg + " aprovado para " + solicitacao.usuario.nomeCompleto + "!");
        } catch (Exception e) {
            flash.error("Erro ao aprovar solicitação: " + e.getMessage());
        }
        
        listarPendentes();
    }
    
    /**
     * Rejeita uma solicitação - apenas ADMINISTRADOR
     */
    @Administrador
    public static void rejeitar(Long id) {
        SolicitacaoAcesso solicitacao = SolicitacaoAcesso.findById(id);
        
        if (solicitacao == null) {
            flash.error("Solicitação não encontrada!");
            listarPendentes();
            return;
        }
        
        try {
            solicitacao.status = StatusSolicitacao.REJEITADO;
            solicitacao.dataResposta = new Date();
            solicitacao.administradorId = Long.parseLong(session.get("usuarioId"));
            solicitacao.save();
            
            flash.success("Solicitação rejeitada para " + solicitacao.usuario.nomeCompleto);
        } catch (Exception e) {
            flash.error("Erro ao rejeitar solicitação: " + e.getMessage());
        }
        
        listarPendentes();
    }
    
    /**
     * Conta solicitações pendentes (para badge no menu)
     */
    @Administrador
    public static long contarPendentes() {
        return SolicitacaoAcesso.count("status = ?1", StatusSolicitacao.PENDENTE);
    }
}