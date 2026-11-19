# Sinalis
 
API RESTful em Java que aproxima pessoas surdas e ouvintes: registra conversas entre Português e Libras e converte frases em sequências de sinais ilustrados.
 
Projeto da **Global Solution FIAP 2026.2** (CitizenTech, Engenharia de Software), disciplina Domain Driven Design - Java.
 
**Deploy:** https://sinalis.onrender.com
> Hospedado no plano gratuito do Render. Após um período sem uso a aplicação "dorme", e o primeiro acesso pode levar cerca de 1 minuto.
 
---
 
## Sobre o projeto
 
A comunicação entre surdos e ouvintes ainda esbarra na falta de ferramentas simples de apoio. O Sinalis oferece uma base para isso: usuários participam de sessões de comunicação, cada sessão guarda o histórico de mensagens, e qualquer frase pode ser convertida em uma sequência de cartões ilustrados de sinais em Libras.
 
## Funcionalidades
 
- **Usuários:** cadastro com perfil `SURDO`, `OUVINTE` ou `OUTRO`.
- **Sessões de comunicação:** início e encerramento de conversas por usuário.
- **Histórico de mensagens:** registro de cada mensagem, com a direção (`PORTUGUES_PARA_LIBRAS` ou `LIBRAS_PARA_PORTUGUES`), salvo no Oracle.
- **Tradutor ilustrado:** converte uma frase em cartões de sinais a partir de um catálogo de 57 sinais, indicando as palavras que não foram encontradas.
- **Interface web:** página única, servida pela própria aplicação, que reúne usuários, sessões e comunicação.
## Tecnologias
 
| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Validation) |
| Banco de dados | Oracle (servidor da FIAP) |
| Build | Maven |
| Deploy | Docker no Render |
| Front-end | HTML, CSS e JavaScript puros |
 
## Arquitetura
 
O código segue a organização em camadas vista em aula, em `br.com.sinalis`:
 
```
model       Entidades JPA (Usuario, SessaoComunicacao, Traducao)
dao         Repositórios Spring Data JPA
bo          Regras de negócio
controller  Endpoints REST
dto         Objetos de requisição
exception   Exceções de negócio e tratamento global (@RestControllerAdvice)
```
 
**Regras de negócio**
- Um usuário com sessões não pode ser excluído.
- Uma sessão com mensagens não pode ser excluída.
- Uma sessão já encerrada não pode ser encerrada novamente.
**Tratamento de erros:** respostas em JSON (`{"erro": "..."}`) com os status `400` (validação ou regra de negócio), `404` (recurso não encontrado) e `409` (registro duplicado ou relacionamento que impede a operação).
 
## Modelo de dados
 
```
SINALIS_USUARIO  1 ──── N  SINALIS_SESSAO  1 ──── N  SINALIS_TRADUCAO
```
 
As tabelas e sequences são criadas automaticamente pelo Hibernate (`ddl-auto=update`). O catálogo de sinais não fica no banco: é carregado do arquivo `sinais.json` na inicialização.
 
## Endpoints
 
Base: `/api`
 
| Recurso | Método | Caminho | Descrição |
|---|---|---|---|
| Usuários | GET | `/usuarios` | Lista usuários |
| | POST | `/usuarios` | Cadastra usuário |
| | GET | `/usuarios/{id}` | Consulta por id |
| | PUT | `/usuarios/{id}` | Atualiza |
| | DELETE | `/usuarios/{id}` | Exclui |
| Sessões | GET | `/sessoes` | Lista sessões |
| | POST | `/sessoes` | Inicia sessão |
| | GET | `/sessoes/{id}` | Consulta por id |
| | PUT | `/sessoes/{id}/encerrar` | Encerra sessão |
| | DELETE | `/sessoes/{id}` | Exclui |
| Traduções | GET | `/traducoes` | Lista (filtro opcional `?sessaoId=`) |
| | POST | `/traducoes` | Registra mensagem |
| | GET | `/traducoes/{id}` | Consulta por id |
| | PUT | `/traducoes/{id}` | Atualiza |
| | DELETE | `/traducoes/{id}` | Exclui |
| Sinais | GET | `/sinais` | Lista o catálogo de sinais |
| | POST | `/sinais/converter` | Converte uma frase em sinais |
 
**Exemplos de corpo (JSON)**
 
```json
// POST /api/usuarios
{ "nome": "Ana", "email": "ana@example.com", "perfil": "SURDO" }
 
// POST /api/sessoes
{ "usuarioId": 1 }
 
// POST /api/traducoes
{ "sessaoId": 1, "direcao": "PORTUGUES_PARA_LIBRAS", "texto": "Bom dia" }
 
// POST /api/sinais/converter
{ "texto": "Eu vou ler" }
```
 
## Executando localmente
 
Requisitos: **JDK 17**, **Maven 3.9+** (ou o Maven do IntelliJ) e acesso ao Oracle da FIAP.
 
A aplicação lê as credenciais do banco de variáveis de ambiente, que devem ser definidas antes de iniciar (no IntelliJ: *Run > Edit Configurations > Environment variables*):
 
| Variável | Descrição | Padrão |
|---|---|---|
| `DB_USER` | Usuário do Oracle | obrigatória |
| `DB_PASSWORD` | Senha do Oracle | obrigatória |
| `DB_URL` | URL JDBC | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` |
| `PORT` | Porta HTTP | `8080` |
 
Em seguida, execute a classe `br.com.sinalis.Aplicacao` (ou `mvn spring-boot:run`) e acesse `http://localhost:8080`.
 
## Deploy
 
O deploy é feito no Render como **Web Service Docker**, a partir do `Dockerfile` da raiz (build com Maven e execução em JRE 17). As variáveis `DB_USER` e `DB_PASSWORD` são configuradas no painel do serviço, e a porta é injetada pelo próprio Render.
 
## Limitações
 
- O tradutor faz **correspondência de vocabulário** com o catálogo de 57 sinais. Não é tradução gramatical completa para Libras, e não há reconhecimento de fala nem interpretação por câmera.
- As imagens e os resultados da conversão não são salvos no banco. Apenas o histórico textual das mensagens é persistido.
- Não há autenticação ou autorização. É um projeto acadêmico e não deve ser usado com dados pessoais reais.
 
