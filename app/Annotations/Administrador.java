package Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação customizada para controle de autorização
 * Baseado no PDF 09 - slides 30-34
 * 
 * Marca ações que só podem ser executadas por ADMINISTRADORES
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Administrador {

}