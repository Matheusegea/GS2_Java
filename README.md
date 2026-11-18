# Sinalis — Global Solution Java 17

MVP acadêmico de gestão de usuários, sessões e registros de comunicação Português/Libras.
**Não realiza tradução automática, reconhecimento de fala nem interpretação por câmera.**

## Requisitos
- Java 17 (Microsoft OpenJDK 17.0.20 compatível)
- Maven 3.9+ (ou Maven integrado ao IntelliJ)
- Acesso à rede e credenciais válidas do Oracle FIAP

## Executar no IntelliJ
1. Abra a pasta `Sinalis` como projeto Maven.
2. Selecione o JDK 17.
3. Em Run > Edit Configurations > Aplicacao > Environment variables, configure `DB_USER` e `DB_PASSWORD` com suas credenciais Oracle. **Não inclua a senha no GitHub.**
4. Execute `br.com.sinalis.Aplicacao`.
5. Abra `http://localhost:8080/` para usar o frontend. A API está em `/api`.

A URL padrão do banco é `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`. Pode ser substituída por `DB_URL`.
O Hibernate está com `ddl-auto=update` para criar/atualizar tabelas e sequences; isso exige permissões DDL no Oracle.

## Endpoints
| Método | Caminho | Função |
|---|---|---|
| GET, POST | /api/usuarios | Listar / cadastrar |
| GET, PUT, DELETE | /api/usuarios/{id} | Consultar / editar / excluir |
| GET, POST | /api/sessoes | Listar / iniciar |
| GET, DELETE | /api/sessoes/{id} | Consultar / excluir |
| PUT | /api/sessoes/{id}/encerrar | Encerrar sessão |
| GET, POST | /api/traducoes | Listar / registrar |
| GET, PUT, DELETE | /api/traducoes/{id} | Consultar / editar / excluir |

`GET /api/traducoes?sessaoId=1` filtra por sessão.

### Exemplos JSON
POST `/api/usuarios`: `{"nome":"Ana","email":"ana@example.com","perfil":"SURDO"}`
POST `/api/sessoes`: `{"usuarioId":1}`
POST `/api/traducoes`: `{"sessaoId":1,"direcao":"PORTUGUES_PARA_LIBRAS","texto":"Bom dia"}`

## Banco
Entidades JPA: `SINALIS_USUARIO` 1:N `SINALIS_SESSAO` 1:N `SINALIS_TRADUCAO`.
A implementação usa interfaces DAO com Spring Data JPA e classes BO com regras de negócio.

## Render
O `Dockerfile` permite criar um Web Service Docker no Render. Configure `DB_USER`, `DB_PASSWORD` e, se necessário, `DB_URL` como variáveis de ambiente no serviço. **É indispensável verificar se o Oracle FIAP aceita conexões vindas do Render**. O deploy não foi executado nem validado.

## Testes
`mvn test` executa um teste básico. **Ainda faltam testes de integração com Oracle e testes de regras de negócio.**

## Segurança
Este MVP não possui autenticação/autorização. Não o exponha com dados pessoais reais sem adicionar controles de acesso, CORS adequado e políticas de privacidade. Não faça commit de senhas.


## Tradutor ilustrado (MVP de 57 sinais)

Acesse `/tradutor.html` ou o link **Tradutor de sinais** no cabeçalho.
O backend expõe `GET /api/sinais` e `POST /api/sinais/converter` (JSON: `{"texto":"Eu vou ler"}`).
O dicionário está em `src/main/resources/sinais.json`, e as 57 imagens otimizadas em `src/main/resources/static/img/sinais/`.
O JSON original tinha entradas repetidas para variantes de palavras; a API lista 57 cartões únicos e reconhece as variantes na conversão.
Palavras não reconhecidas são listadas separadamente. O algoritmo faz correspondência de vocabulário, não tradução gramatical completa de português para Libras.
Não há alterações nas tabelas Oracle existentes; o catálogo é carregado do JSON na inicialização.

## Interface unificada (novembro/2026)

- Abra `http://localhost:8080/`: usuários, sessões e comunicação na mesma página.
- Mensagens de comunicação são persistidas no Oracle via `/api/traducoes`.
- Ao salvar ou clicar em **Ver sinais** no histórico, o navegador consulta `/api/sinais/converter` e mostra as ilustrações correspondentes.
- Imagens e resultados de conversão não são persistidos no banco. A visualização é temporária e desaparece ao atualizar a página; o histórico textual continua salvo.
- Catálogo limitado a 57 sinais ilustrados; palavras sem correspondência são identificadas.
- Não é tradução gramatical completa para Libras.
- A API Java existente e as entidades JPA foram preservadas.
