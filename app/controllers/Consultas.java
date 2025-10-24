package controllers;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import models.Cliente;
import models.Consulta;
import models.Status;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Consultas extends Controller {
    
    public static void form(Long clienteId) {
        if (clienteId == null) {
            flash.error("ID do cliente é obrigatório para agendar consulta!");
            Clientes.listar(null);
            return;
        }
        
        Cliente cliente = Cliente.findById(clienteId);
        if (cliente == null) {
            flash.error("Cliente não encontrado!");
            Clientes.listar(null);
            return;
        }
        
        render(cliente);
    }
    
    public static void salvar(Long clienteId, String dataConsulta) {
        if (clienteId == null) {
            flash.error("ID do cliente é obrigatório!");
            Clientes.listar(null);
            return;
        }
        
        if (dataConsulta == null || dataConsulta.trim().isEmpty()) {
            flash.error("Data da consulta é obrigatória!");
            form(clienteId);
            return;
        }
        
        Cliente cliente = Cliente.findById(clienteId);
        if (cliente == null) {
            flash.error("Cliente não encontrado!");
            Clientes.listar(null);
            return;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = sdf.parse(dataConsulta);
            
            Consulta consulta = new Consulta();
            consulta.dataConsulta = parsedDate;
            consulta.cliente = cliente;
            consulta.save();
            
            flash.success("Consulta agendada, obrigado pela preferência!");
            
            Application.index();
            
        } catch (Exception e) {
            flash.error("Erro ao agendar consulta. Tente novamente.");
            form(clienteId);
        }
    }
    
    public static void listar() {
        List<Consulta> consultas = Consulta.find("order by dataConsulta desc").fetch();
        render(consultas);
    }
    
    public static long contarConsultas() {
        return Consulta.count();
    }
    
    public static long contarConsultasHoje() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String hoje = sdf.format(new Date());
        return Consulta.count("DATE(dataConsulta) = ?", hoje);
    }
    
    public static long contarConsultasSemana() {
        return Consulta.count("dataConsulta >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
    }
}