package controllers;

import models.SolicitacaoAcesso;
import models.StatusSolicitacao;
import models.Usuario;
import models.Perfil;
import play.mvc.Controller;

public class Logins extends Controller {
    
    // Exibe formulário de login
    public static void form() {
        render();
    }
    
    // Nova action: exibe formulário de cadastro
    public static void cadastro() {
        render();
    }
    
    // Processa o login
    public static void logar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || 
            senha == null || senha.trim().isEmpty()) {
            flash.error("Por favor, preencha login e senha");
            form();
            return;
        }
        
        Usuario usuario = Usuario.autenticar(login, senha);
        
        if (usuario == null) {
            flash.error("Login ou senha inválidos!");
            form();
            return;
        }
        
        // Se for ADMINISTRADOR, pode entrar direto
        if (usuario.perfil == Perfil.ADMINISTRADOR) {
            session.put("usuarioLogado", usuario.login);
            session.put("perfilUsuario", usuario.perfil.toString());
            session.put("nomeUsuario", usuario.nomeCompleto);
            session.put("usuarioId", usuario.id.toString());
            
            flash.success("Bem-vindo, " + usuario.nomeCompleto + "!");
            Application.index();
            return;
        }
        
        // Para FUNCIONARIO, verifica aprovação
        SolicitacaoAcesso solicitacao = SolicitacaoAcesso.find(
            "usuario = ?1 and status = ?2 order by dataResposta desc", 
            usuario, StatusSolicitacao.APROVADO
        ).first();
        
        if (solicitacao == null) {
            // Verifica se já existe solicitação pendente
            SolicitacaoAcesso pendente = SolicitacaoAcesso.find(
                "usuario = ?1 and status = ?2", 
                usuario, StatusSolicitacao.PENDENTE
            ).first();
            
            if (pendente == null) {
                // Cria nova solicitação
                SolicitacaoAcesso nova = new SolicitacaoAcesso();
                nova.usuario = usuario;
                nova.save();
            }
            
            flash.error("Seu acesso está aguardando aprovação do administrador!");
            form();
            return;
        }
        
        // Verifica se foi rejeitado
        SolicitacaoAcesso rejeitada = SolicitacaoAcesso.find(
            "usuario = ?1 and status = ?2", 
            usuario, StatusSolicitacao.REJEITADO
        ).first();
        
        if (rejeitada != null && (solicitacao == null || 
            rejeitada.dataResposta.after(solicitacao.dataResposta))) {
            flash.error("Seu acesso foi negado pelo administrador. Entre em contato para mais informações.");
            form();
            return;
        }
        
        // Verifica tipo de acesso
        if (solicitacao.tipoAcesso == models.TipoAcesso.UNICO) {
            if (solicitacao.acessoUtilizado) {
                flash.error("Seu acesso único já foi utilizado. Solicite nova aprovação ao administrador.");
                form();
                return;
            }
            // Marca como utilizado
            solicitacao.acessoUtilizado = true;
            solicitacao.save();
        }
        
        // Permite o login
        session.put("usuarioLogado", usuario.login);
        session.put("perfilUsuario", usuario.perfil.toString());
        session.put("nomeUsuario", usuario.nomeCompleto);
        session.put("usuarioId", usuario.id.toString());
        session.put("solicitacaoId", solicitacao.id.toString());
        
        flash.success("Bem-vindo, " + usuario.nomeCompleto + "!");
        Application.index();
    }
    
    // Processa o cadastro de novo usuário
    public static void registrar(Usuario usuario) {
        // Validações
        if (usuario.nomeCompleto == null || usuario.nomeCompleto.trim().isEmpty()) {
            flash.error("Nome completo é obrigatório");
            cadastro();
            return;
        }
        
        if (usuario.email == null || usuario.email.trim().isEmpty()) {
            flash.error("Email é obrigatório");
            cadastro();
            return;
        }
        
        if (!usuario.email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            flash.error("Email inválido!");
            cadastro();
            return;
        }
        
        if (usuario.login == null || usuario.login.trim().isEmpty()) {
            flash.error("Login é obrigatório");
            cadastro();
            return;
        }
        
        if (usuario.senha == null || usuario.senha.trim().isEmpty()) {
            flash.error("Senha é obrigatória");
            cadastro();
            return;
        }
        
        // Verifica se login já existe
        if (Usuario.loginExiste(usuario.login, null)) {
            flash.error("Este login já está cadastrado! Escolha outro.");
            cadastro();
            return;
        }
        
        try {
            usuario.status = models.Status.ATIVO;
            usuario.perfil = Perfil.FUNCIONARIO; // Sempre FUNCIONARIO no cadastro
            usuario.save();
            
            // Cria solicitação de acesso automaticamente
            SolicitacaoAcesso solicitacao = new SolicitacaoAcesso();
            solicitacao.usuario = usuario;
            solicitacao.save();
            
            flash.success("Cadastro realizado! Aguarde a aprovação do administrador para fazer login.");
            form();
        } catch (Exception e) {
            flash.error("Erro ao cadastrar: " + e.getMessage());
            cadastro();
        }
    }
    
    public static void logout() {
        session.clear();
        flash.success("Você saiu do sistema!");
        form();
    }
}