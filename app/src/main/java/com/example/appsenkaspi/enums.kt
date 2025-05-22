package com.example.appsenkaspi



enum class Cargo {
    COORDENADOR,
    GESTOR,
    APOIO
}

enum class StatusPilar {
    ATIVO,
    EXCLUIDO,
    VENCIDO,
    VENCIDA,
    EXCLUIDA,
    CONCLUIDA
}

enum class StatusAcao {
    PLANEJADA,
    EM_ANDAMENTO,
    CONCLUIDA,
    EXCLUIDA,
    VENCIDA
}

enum class StatusAtividade {
    PENDENTE,
    EM_ANDAMENTO,
    CONCLUIDA,
    EXCLUIDA,
    VENCIDA
}

enum class PrioridadeAtividade {
    ALTA,
    MEDIA,
    BAIXA
}

enum class StatusRequisicao {
    PENDENTE,
    ACEITA,
    RECUSADA
}

enum class TipoRequisicao {
    CRIAR_ATIVIDADE,
    EDITAR_ATIVIDADE,
    COMPLETAR_ATIVIDADE,
    CRIAR_ACAO,
    EDITAR_ACAO,
    ATIVIDADE_PARA_VENCER
}

enum class StatusNotificacao {
    NAO_LIDA,
    LIDA,
    ARQUIVIDA,
    NOVA
}

enum class TipoDeNotificacao {
    // ➤ Notificações enviadas ao Coordenador (pedidos do Apoio)
    PEDIDO_CRIACAO_ACAO,
    PEDIDO_CRIACAO_ATIVIDADE,
    PEDIDO_EDICAO_ACAO,
    PEDIDO_EDICAO_ATIVIDADE,
    PEDIDO_CONFIRMACAO_ATIVIDADE,

    // ➤ Notificações enviadas ao Apoio (respostas do Coordenador)
    CRIACAO_ACAO_ACEITA,
    CRIACAO_ATIVIDADE_ACEITA,
    EDICAO_ACAO_ACEITA,
    EDICAO_ATIVIDADE_ACEITA,
    CONFIRMACAO_ATIVIDADE_ACEITA
}
