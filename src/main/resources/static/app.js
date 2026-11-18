const $ = (id) => document.getElementById(id);
let usuarios = [];
let sessoes = [];
let traducoes = [];

async function requisitar(caminho, opcoes = {}) {
    const resposta = await fetch(`/api${caminho}`, {
        ...opcoes,
        headers: { 'Content-Type': 'application/json', ...(opcoes.headers || {}) }
    });
    if (!resposta.ok) {
        let mensagem = `Erro HTTP ${resposta.status}`;
        try { mensagem = (await resposta.json()).erro || mensagem; } catch (_) {}
        throw new Error(mensagem);
    }
    return resposta.status === 204 ? null : resposta.json();
}

function avisar(mensagem) {
    $('mensagem').textContent = mensagem;
}

function botao(rotulo, acao, id, classe = '') {
    const elemento = document.createElement('button');
    elemento.type = 'button';
    elemento.textContent = rotulo;
    elemento.className = classe;
    elemento.addEventListener('click', () => executar(() => acao(id)));
    return elemento;
}

function linha(container, titulo, detalhe, acoes = []) {
    const item = document.createElement('div');
    item.className = 'item';
    const conteudo = document.createElement('div');
    const nome = document.createElement('strong');
    nome.textContent = titulo;
    const subtitulo = document.createElement('small');
    subtitulo.textContent = detalhe;
    conteudo.append(nome, subtitulo);
    const botoes = document.createElement('div');
    botoes.className = 'acoes';
    acoes.forEach((acao) => botoes.append(acao));
    item.append(conteudo, botoes);
    container.append(item);
}

function preencherSelect(id, itens, titulo, texto) {
    const seletor = $(id);
    const valor = seletor.value;
    seletor.replaceChildren();
    const opcao = new Option(titulo, '');
    opcao.disabled = true;
    seletor.add(opcao);
    itens.forEach((item) => seletor.add(new Option(texto(item), String(item.id))));
    seletor.value = itens.some((item) => String(item.id) === valor) ? valor : '';
}

async function carregar() {
    [usuarios, sessoes, traducoes] = await Promise.all([
        requisitar('/usuarios'), requisitar('/sessoes'), requisitar('/traducoes')
    ]);
    renderizar();
}

function renderizar() {
    preencherSelect('sessaoUsuario', usuarios, 'Selecione um usuário', (u) => `${u.nome} (${u.perfil})`);
    preencherSelect('traducaoSessao', sessoes.filter((s) => !s.encerradaEm), 'Selecione uma sessão ativa', (s) => `Sessão #${s.id} — ${s.usuario.nome}`);
    $('listaUsuarios').replaceChildren();
    usuarios.forEach((u) => linha($('listaUsuarios'), u.nome, `${u.email} • ${u.perfil}`, [
        botao('Editar', editarUsuario, u.id), botao('Excluir', excluirUsuario, u.id, 'perigo')
    ]));
    $('listaSessoes').replaceChildren();
    sessoes.forEach((s) => linha($('listaSessoes'), `Sessão #${s.id} — ${s.usuario.nome}`, s.encerradaEm ? 'Encerrada' : 'Ativa', [
        ...(!s.encerradaEm ? [botao('Encerrar', encerrarSessao, s.id)] : []),
        botao('Excluir', excluirSessao, s.id, 'perigo')
    ]));
    $('listaTraducoes').replaceChildren();
    traducoes.forEach((t) => linha($('listaTraducoes'), `#${t.id} — ${t.direcao.replaceAll('_', ' ')}`, `Sessão #${t.sessao.id} • ${t.texto}`, [
        botao('Ver sinais', visualizarRegistro, t.id, 'secundario'),
        ...(!t.sessao.encerradaEm ? [botao('Editar', editarTraducao, t.id, 'secundario')] : []),
        botao('Excluir', excluirTraducao, t.id, 'perigo')
    ]));
}

async function executar(acao) {
    try { await acao(); await carregar(); } catch (erro) { avisar(erro.message); }
}

function editarUsuario(id) {
    const u = usuarios.find((item) => item.id === id);
    $('usuarioId').value = u.id;
    $('nome').value = u.nome;
    $('email').value = u.email;
    $('perfil').value = u.perfil;
    $('usuarios').scrollIntoView();
}
async function excluirUsuario(id) {
    if (confirm('Excluir usuário?')) { await requisitar(`/usuarios/${id}`, { method: 'DELETE' }); avisar('Usuário excluído.'); }
}
async function encerrarSessao(id) {
    await requisitar(`/sessoes/${id}/encerrar`, { method: 'PUT' }); avisar('Sessão encerrada.');
}
async function excluirSessao(id) {
    if (confirm('Excluir sessão?')) { await requisitar(`/sessoes/${id}`, { method: 'DELETE' }); avisar('Sessão excluída.'); }
}
function editarTraducao(id) {
    const t = traducoes.find((item) => item.id === id);
    $('traducaoId').value = t.id;
    $('traducaoSessao').value = t.sessao.id;
    $('direcao').value = t.direcao;
    $('texto').value = t.texto;
    $('traducoes').scrollIntoView();
    visualizarTexto(t.texto);
}
async function excluirTraducao(id) {
    if (confirm('Excluir registro?')) { await requisitar(`/traducoes/${id}`, { method: 'DELETE' }); avisar('Registro excluído.'); }
}
$('limparUsuario').addEventListener('click', () => { $('formUsuario').reset(); $('usuarioId').value = ''; });
$('limparTraducao').addEventListener('click', () => { $('formTraducao').reset(); $('traducaoId').value = ''; ocultarSinais(); });
$('fecharSinais').addEventListener('click', ocultarSinais);
$('formUsuario').addEventListener('submit', (evento) => {
    evento.preventDefault();
    executar(async () => {
        const id = $('usuarioId').value;
        await requisitar(id ? `/usuarios/${id}` : '/usuarios', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify({ nome: $('nome').value, email: $('email').value, perfil: $('perfil').value })
        });
        $('formUsuario').reset(); $('usuarioId').value = ''; avisar('Usuário salvo.');
    });
});
$('formSessao').addEventListener('submit', (evento) => {
    evento.preventDefault();
    executar(async () => {
        await requisitar('/sessoes', { method: 'POST', body: JSON.stringify({ usuarioId: Number($('sessaoUsuario').value) }) });
        avisar('Sessão iniciada.');
    });
});
$('formTraducao').addEventListener('submit', (evento) => {
    evento.preventDefault();
    executar(async () => {
        const id = $('traducaoId').value;
        await requisitar(id ? `/traducoes/${id}` : '/traducoes', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify({ sessaoId: Number($('traducaoSessao').value), direcao: $('direcao').value, texto: $('texto').value })
        });
        const textoSalvo = $('texto').value;
        $('formTraducao').reset(); $('traducaoId').value = ''; avisar('Mensagem salva no Oracle.');
        await visualizarTexto(textoSalvo);
    });
});
executar(carregar);

// As imagens são renderizadas somente no navegador; nenhum caminho de imagem é gravado no Oracle.
function ocultarSinais() {
    $('visualizacaoSinais').hidden = true;
    $('resultadoSinais').replaceChildren();
    $('desconhecidas').hidden = true;
}
function visualizarRegistro(id) {
    const registro = traducoes.find(item => item.id === id);
    if (registro) return visualizarTexto(registro.texto);
}
function criarCartaoSinal(sinal) {
    const figura = document.createElement('figure');
    figura.className = 'cartaoSinal';
    const imagem = document.createElement('img');
    imagem.src = sinal.imagem;
    imagem.alt = `Ilustração do sinal ${sinal.rotulo}`;
    imagem.loading = 'lazy';
    const legenda = document.createElement('figcaption');
    legenda.textContent = sinal.rotulo;
    figura.append(imagem, legenda);
    return figura;
}
async function visualizarTexto(texto) {
    const area = $('visualizacaoSinais');
    area.hidden = false;
    $('fraseVisualizada').textContent = texto;
    $('statusSinais').textContent = 'Procurando sinais disponíveis...';
    $('resultadoSinais').replaceChildren();
    $('desconhecidas').hidden = true;
    area.scrollIntoView({behavior: 'smooth', block: 'nearest'});
    try {
        // O endpoint Java consulta o JSON de sinais; não altera o histórico.
        const resposta = await fetch('/api/sinais/converter', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({texto: texto.slice(0, 500)})
        });
        if (!resposta.ok) throw new Error('Não foi possível buscar os sinais desta mensagem.');
        const dados = await resposta.json();
        $('resultadoSinais').replaceChildren(...dados.sinais.map(criarCartaoSinal));
        $('statusSinais').textContent = `${dados.sinais.length} sinal(is) encontrado(s) no catálogo de ${dados.totalSinaisDisponiveis}.`;
        if (dados.palavrasNaoEncontradas.length) {
            $('desconhecidas').textContent = `Sem imagem para: ${dados.palavrasNaoEncontradas.join(', ')}.`;
            $('desconhecidas').hidden = false;
        }
    } catch (erro) {
        $('statusSinais').textContent = erro.message;
    }
}
