package controllers;

import Annotations.Administrador;
import java.util.List;
import models.Cliente;
import models.Status;
import play.mvc.Controller;
import play.mvc.With;
import play.db.jpa.Blob;
import java.io.File;
import java.io.FileInputStream;

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
            clientes = Cliente.find("(lower(nomeCompleto) like ?1 or lower(email) like ?1 or cpf like ?1) and status <> ?2",
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
    public static void salvar(Cliente cliente, File foto) {
        // Validações conforme requisitos do PDF - Regras de Validação
        if (cliente.nomeCompleto == null || cliente.nomeCompleto.trim().isEmpty()) {
            flash.error("Nome completo é obrigatório");
            render("Clientes/form.html", cliente);
            return;
        }
        
        // VALIDAÇÃO DO CPF
        if (cliente.cpf == null || cliente.cpf.trim().isEmpty()) {
            flash.error("CPF é obrigatório");
            render("Clientes/form.html", cliente);
            return;
        }
        
        // Remove formatação para salvar
        String cpfLimpo = cliente.cpf.replaceAll("[^0-9]", "");
        
        // Valida formato do CPF
        if (!Cliente.validarCPF(cpfLimpo)) {
            flash.error("CPF inválido! Verifique os números digitados.");
            render("Clientes/form.html", cliente);
            return;
        }
        
        // Verifica se CPF já existe
        if (Cliente.cpfExiste(cpfLimpo, cliente.id)) {
            flash.error("Este CPF já está cadastrado no sistema!");
            render("Clientes/form.html", cliente);
            return;
        }
        
        // Salva CPF sem formatação
        cliente.cpf = cpfLimpo;
        
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
            // Se está editando, recupera o cliente existente
            if (cliente.id != null) {
                Cliente clienteExistente = Cliente.findById(cliente.id);
                if (clienteExistente != null) {
                    clienteExistente.nomeCompleto = cliente.nomeCompleto;
                    clienteExistente.cpf = cliente.cpf;
                    clienteExistente.email = cliente.email;
                    clienteExistente.telefone = cliente.telefone;
                    
                    // Processa a foto se foi enviada
                    if (foto != null && foto.exists()) {
                        clienteExistente.foto = new Blob();
                        clienteExistente.foto.set(new FileInputStream(foto), foto.getName());
                    }
                    
                    clienteExistente.save();
                    cliente = clienteExistente;
                }
            } else {
                // Novo cliente
                if (foto != null && foto.exists()) {
                    cliente.foto = new Blob();
                    cliente.foto.set(new FileInputStream(foto), foto.getName());
                }
                cliente.save();
            }
            
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
    
    
   //  Remove a foto do cliente via AJAX
     
    public static void removerFoto(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente != null) {
            cliente.foto = null;
            cliente.save();
            renderJSON("{\"success\": true, \"message\": \"Foto removida com sucesso!\"}");
        } else {
            renderJSON("{\"success\": false, \"message\": \"Cliente não encontrado!\"}");
        }
    }
    
   
     // Retorna a foto do cliente
    
    public static void foto(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente != null && cliente.foto != null && cliente.foto.exists()) {
            renderBinary(cliente.foto.get());
        } else {
            notFound();
        }
    }
   
    
     // Verifica se CPF já existe usando AJAX
     
    public static void verificarCPF(String cpf, Long id) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        boolean existe = Cliente.cpfExiste(cpfLimpo, id);
        renderJSON("{\"existe\": " + existe + "}");
    }
    
    
    // Apenas ADMINISTRADORES podem remover clientes
     
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