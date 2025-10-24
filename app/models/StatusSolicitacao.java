package models;

public enum StatusSolicitacao {
    PENDENTE,   // Aguardando aprovação do administrador
    APROVADO,   // Administrador aprovou o acesso
    REJEITADO   // Administrador rejeitou o acesso
}