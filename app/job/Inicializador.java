package job;

import models.Perfil;
import models.Status;
import models.Usuario;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 * Inicializador da aplicação - cria usuário administrador padrão
 */
@OnApplicationStart
public class Inicializador extends Job {
    
    public void doJob() {
        // Verifica se já existe o administrador padrão
        Usuario adminPadrao = Usuario.find("email = ?1", "Claracamara@gmail.com").first();
        
        if (adminPadrao == null) {
            // Cria o administrador padrão
            Usuario admin = new Usuario();
            admin.nomeCompleto = "Clara Camara";
            admin.email = "Claracamara@gmail.com";
            admin.login = "claracamara";
            admin.senha = "090909";
            admin.perfil = Perfil.ADMINISTRADOR;
            admin.status = Status.ATIVO;
            admin.save();
            
            System.out.println("✅ Administrador padrão criado com sucesso!");
            System.out.println("   Email: Claracamara@gmail.com");
            System.out.println("   Senha: 090909");
        } else {
            System.out.println("ℹ️  Administrador padrão já existe no sistema.");
        }
    }
}