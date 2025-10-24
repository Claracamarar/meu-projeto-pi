package controllers;

import Annotations.Administrador;
import java.util.List;
import models.Cliente;
import models.Status;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Clientes extends Controller {
    
    // FUNCIONARIO e ADMINISTRADOR podem acessar
    public static void form() {
        Cliente cliente = new Cliente(); 
        render(cliente);
    }
    
    // FUNCIONARIO e ADMINISTRADOR podem acessar
    public static void listar(String termo) {
        List<Cliente> clientes;
        if (termo == null || termo.isEmpty()) {
            clientes = Cliente.find("status <> ?1", Status.INATIVO).fetch();
        } else {
            clientes = Cliente.find("(lower(nomeCompleto) like ?1 or lower(email) like ?1) and status <> ?2",
                "%" + termo.toLowerCase() + "%", Status.INATIVO).fetch();
        }
        render(clientes, termo);
    }
    
    // FUNCIONARIO e ADMINISTRADOR podem acessar
    public static void editar(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            flash.error("Cliente não encontrado!");
            listar(null);
            return;
        }
        render("Clientes/form.html", cliente);
    }
    
    // FUNCIONARIO e ADMINISTRADOR podem acessar
    public static void salvar(Cliente cliente) {
        // Validações conforme requisitos do PDF - Regras de Validação
        if (cliente.nomeCompleto == null || cliente.nomeCompleto.trim().isEmpty()) {
            flash.error("Nome completo é obrigatório");
            render("Clientes/form.html", cliente);
            return;
        }
        
        if (cliente.email == null || cliente.email.trim().isEmpty()) {
            flash.error("Email é obrigatório");
            render("Clientes/form.html", cliente);
            return;
        }
        
        // Validação de formato de email
        if (!cliente.email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            flash.error("Email inválido! Use o formato: exemplo@email.com");
            render("Clientes/form.html", cliente);
            return;
        }
        
        if (cliente.telefone == null || cliente.telefone.trim().isEmpty()) {
            flash.error("Telefone é obrigatório");
            render("Clientes/form.html", cliente);
            return;
        }
        
        try {
            cliente.save();
            flash.success("Cliente salvo com sucesso!");
        } catch (Exception e) {
            flash.error("Erro ao salvar cliente: " + e.getMessage());
            render("Clientes/form.html", cliente);
            return;
        }
        
        detalhar(cliente.id);
    }
    
    // FUNCIONARIO e ADMINISTRADOR podem acessar
    public static void detalhar(long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            flash.error("Cliente não encontrado!");
            listar(null);
            return;
        }
        render(cliente);
    }
    
    /**
     * Apenas ADMINISTRADORES podem remover clientes
     */
    @Administrador
    public static void remover(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente != null) {
            cliente.status = Status.INATIVO;
            cliente.save();
            flash.success("Cliente removido com sucesso!");
        }
        listar(null);
    }
}