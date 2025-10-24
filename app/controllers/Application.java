package controllers;

import java.util.Date;

import models.Cliente;
import models.Consulta;
import models.Status;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import play.mvc.Controller;
import play.mvc.With;

@With(Seguranca.class)
public class Application extends Controller {

 
	   public static void index() {
	        long clientesAtivos = Cliente.count("status = ?1", Status.ATIVO);
	        long totalConsultas = Consulta.count();
	        long consultasHoje = contarConsultasHoje();
	        long consultasSemana = contarConsultasSemana();
	        
	        render(clientesAtivos, totalConsultas, consultasHoje, consultasSemana);
	    }
	    
    private static long contarConsultasHoje() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String hoje = sdf.format(new Date());
            
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date inicioHoje = cal.getTime();
            
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date fimHoje = cal.getTime();
            
            return Consulta.count("dataConsulta >= ?1 AND dataConsulta <= ?2", inicioHoje, fimHoje);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private static long contarConsultasSemana() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date inicioSemana = cal.getTime();
            
            return Consulta.count("dataConsulta >= ?1", inicioSemana);
        } catch (Exception e) {
            return 0;
        }
    }
}