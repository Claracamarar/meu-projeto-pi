package controllers;

import Annotations.Administrador;
import java.util.List;
import models.Perfil;
import models.Status;
import models.Usuario;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Usuarios extends Controller {
    
    @Administrador
    public static void listar() {
        List<Usuario> usuarios = Usuario.find("status = ?1 order by nomeCompleto", Status.ATIVO).fetch();
        render(usuarios);
    }
    
    @Administrador
    public static void form() {
        Usuario usuario = new Usuario();
        List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
        render(usuario, perfis);
    }
    
    @Administrador
    public static void editar(Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario == null) {
            flash.error("Usuário não encontrado!");
            listar();
            return;
        }
        List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
        render("Usuarios/form.html", usuario, perfis);
    }
    
    @Administrador
    public static void salvar(Usuario usuario) {
        // Validações
        if (usuario.nomeCompleto == null || usuario.nomeCompleto.trim().isEmpty()) {
            flash.error("Nome completo é obrigatório");
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
            return;
        }
        
        if (usuario.login == null || usuario.login.trim().isEmpty()) {
            flash.error("Login é obrigatório");
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
            return;
        }
        
        if (usuario.senha == null || usuario.senha.trim().isEmpty()) {
            flash.error("Senha é obrigatória");
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
            return;
        }
        
        if (usuario.email == null || usuario.email.trim().isEmpty()) {
            flash.error("Email é obrigatório");
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
            return;
        }
        
        // Verifica se login já existe
        if (Usuario.loginExiste(usuario.login, usuario.id)) {
            flash.error("Login já cadastrado no sistema!");
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
            return;
        }
        
        try {
            usuario.save();
            flash.success("Usuário salvo com sucesso!");
            listar();
        } catch (Exception e) {
            flash.error("Erro ao salvar usuário: " + e.getMessage());
            List<Perfil> perfis = java.util.Arrays.asList(Perfil.values());
            render("Usuarios/form.html", usuario, perfis);
        }
    }
    
    @Administrador
    public static void remover(Long id) {
        Usuario usuario = Usuario.findById(id);
        if (usuario != null) {
            String loginLogado = session.get("usuarioLogado");
            if (usuario.login.equals(loginLogado)) {
                flash.error("Você não pode remover seu próprio usuário!");
                listar();
                return;
            }
            
            usuario.status = Status.INATIVO;
            usuario.save();
            flash.success("Usuário removido com sucesso!");
        }
        listar();
    }
}