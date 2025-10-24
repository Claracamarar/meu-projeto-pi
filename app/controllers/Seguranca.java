package controllers;

import Annotations.Administrador;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * Interceptador para controle de autenticação e autorização
 * Baseado no PDF 09 - Interceptadores e Sessão
 */
public class Seguranca extends Controller {
	
	/**
	 * Verifica se o usuário está autenticado
	 * PDF 09 - slide 19-20
	 */
	@Before(unless={"form", "logar"})
	static void verificarAutenticacao() {
		if (!session.contains("usuarioLogado")) {
			flash.error("Você deve fazer login para acessar o sistema.");
			Logins.form();
		}
	}
	
	/**
	 * Verifica se o usuário tem perfil de ADMINISTRADOR
	 * PDF 09 - slides 30-34
	 */
	@Before
	static void verificarAdministrador() {
		// Recupera anotação @Administrador da ação
		Administrador adminAnnotation = getActionAnnotation(Administrador.class);
		
		if (adminAnnotation != null) {
			String perfil = session.get("perfilUsuario");
			
			// Se não for ADMINISTRADOR, bloqueia acesso
			if (!"ADMINISTRADOR".equals(perfil)) {
				flash.error("Acesso negado! Apenas administradores podem executar esta ação.");
				Application.index();
			}
		}
	}
}