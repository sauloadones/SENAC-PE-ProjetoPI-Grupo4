## 🏢 AppSenKaspi - Projeto Compliance Senac
Este projeto é um sistema Android desenvolvido para o gerenciamento de pilares e atividades no contexto do Compliance do Senac. Ele facilita a criação, acompanhamento e automação de processos com funcionalidades como geração de relatórios, controle de progresso e perfis com permissões distintas.

## 📝 Visão Geral
O sistema foi criado para organizar e automatizar tarefas relacionadas à conformidade (compliance), permitindo o gerenciamento de pilares, subpilares, ações e atividades por diferentes perfis de usuários. Também possibilita a análise de logs e geração de dashboards com base na produção de atividades realizadas.

## ✅ Funcionalidades Principais
Cadastro e gerenciamento de pilares, subpilares, ações e atividades

Atribuição de atividades a funcionários com progresso individual

Processamento de porcentagem de execução por atividade realizada

Geração de relatórios de produção e desempenho

Perfis de acesso diferenciados com funcionalidades específicas

Banco de dados inicial pré-carregado com usuários fictícios para testes

Interface responsiva e moderna com animações

## 🛠 Tecnologias Utilizadas
Mobile: Kotlin

Banco de Dados: SQLite (com Room – Android Jetpack)

Persistência: Room + TypeConverters personalizados

Concorrência: Kotlin Coroutines

API: Python (a definir detalhes do backend)

## 🗃️ Banco de Dados
O banco de dados local (appsenkaspi.db) é criado automaticamente ao iniciar o app, com dados pré-populados por meio de um RoomDatabase.Callback.

Principais entidades:
PilarEntity, SubpilarEntity, AcaoEntity, AtividadeEntity

FuncionarioEntity (com perfis, login e permissões)

Entidades relacionais: AcaoFuncionarioEntity, AtividadeFuncionarioEntity

ChecklistItemEntity, RequisicaoEntity

O acesso aos dados é realizado por meio de DAOs (Data Access Objects) para garantir separação de responsabilidades e acesso seguro às operações de CRUD.

## 🚀 Como Executar o Projeto
Clone o repositório:

bash
Copiar
Editar
git clone https://github.com/seu-usuario/appsenkaspi.git
cd appsenkaspi
Abra o projeto no Android Studio.

Sincronize o Gradle e construa o projeto.

Execute em um emulador Android ou dispositivo físico.

## 📦 Requisitos do Sistema

Dependência 1: A definir (API externa)

Dependência 2: Banco de dados SQLite (Room)

Dependência 3: Android Studio + ambiente Kotlin configurado

## 👥 Contribuições dos Membros do Grupo
Saulo – Responsável pela implementação de funcionalidades e atualização do banco de dados.

Andrey – Responsável pelo design da interface, animações visuais e aprimoramento da experiência do usuário.

Matheus – Responsável pela integração e implementações de API.

João – Responsável pela gestão de erros e correção de bugs.

Lucas – Responsável pela criação das telas iniciais do escopo do projeto.

Vitor – Responsável pela criação das telas iniciais do escopo do projeto.

## Links
[Notion](https://www.notion.so/Sistema-de-Ouvidoria-do-SENAC-1a6cf81c640d8080b6d3f4cd051740fa?pvs=4) - Documentação de Requisitos

[Trello](https://trello.com/invite/b/67ec3fa72b0388fbbbc61382/ATTI65e83e7e71fbc4b6e05d9965b82e2f0fDD78737D/projeto-integrador) - Ferramenta de Gerenciamento

[Figma](https://www.figma.com/design/hzO79KFKydvrj3Y4NApAbE/Projeto-do-Grupo-4?node-id=2-6) - Prototipação de Alto Nivel

[Miro](https://miro.com/app/board/uXjVIFXhKPU=/?inviteKey=VzVFWVVWTWl6dU13cFJuN2wvVjE4UytHVXlhak5KNWtXblhKY2x5ZkRkd0pXdlYvVDlqZTZHdjlJeWRBTzB0S1kzT1E2RE84bEt2VDkzTzNXVXBHaURrZlk4NlVtVmc4SzJVQllVM0hPZHlYQ0hNcjQ0a0JJR2Mra0tnVFNwTFJyVmtkMG5hNDA3dVlncnBvRVB2ZXBnPT0hdjE=) - Personas e Historia
