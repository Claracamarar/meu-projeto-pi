package models;

/**
 * Enum com apenas 2 perfis: ADMINISTRADOR e FUNCIONARIO
 */
public enum Perfil {
    ADMINISTRADOR,  // Acesso total ao sistema
    FUNCIONARIO     // Pode cadastrar clientes e agendar consultas
}