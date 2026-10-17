package siga;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestesSiga {
    private static int verificacoes;

    private static void verificar(boolean condicao, String descricao) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(descricao);
        }
    }

    private static void esperarFalha(Class<? extends RuntimeException> tipo, Runnable acao) {
        try {
            acao.run();
            throw new AssertionError("Falha esperada nao ocorreu: " + tipo.getSimpleName());
        } catch (RuntimeException erro) {
            verificar(tipo.isInstance(erro), "Tipo de falha incorreto: " + erro);
        }
    }

    public static void main(String[] args) {
        AlunoDAO dao = new AlunoDAOMemoria();
        Aluno maria = new Aluno("Maria", "001", 8.5);
        verificar(dao.listarTodos().isEmpty(), "DAO deveria iniciar vazio");
        verificar(dao.buscarPorMatricula("ausente") == null, "Busca ausente deve retornar null");
        dao.inserir(maria);
        esperarFalha(IllegalStateException.class, () -> dao.inserir(new Aluno("Outra", "001", 7)));
        verificar(dao.buscarPorMatricula("001") == maria, "Duplicidade substituiu registro");
        List<Aluno> copia = dao.listarTodos();
        copia.add(new Aluno("Intruso", "999", 10));
        verificar(dao.buscarPorMatricula("999") == null, "Lista externa inseriu no DAO");
        copia.clear();
        verificar(dao.listarTodos().size() == 1, "Lista externa apagou armazenamento");
        Aluno atualizada = new Aluno("Maria Atualizada", "001", 9);
        dao.atualizar(atualizada);
        verificar(dao.buscarPorMatricula("001") == atualizada, "Atualizacao nao substituiu registro");
        verificar(dao.listarTodos().size() == 1, "Atualizacao duplicou registro");
        esperarFalha(IllegalStateException.class, () -> dao.atualizar(new Aluno("Ausente", "000", 7)));
        esperarFalha(IllegalStateException.class, () -> dao.remover("000"));
        dao.remover("001");
        verificar(dao.listarTodos().isEmpty(), "Remocao nao funcionou");
        esperarFalha(IllegalStateException.class, () -> dao.remover("001"));

        ServicoAluno servico = FabricaServicoAluno.criarEmMemoria();
        servico.cadastrar(maria);
        verificar(servico.consultar("001") == maria, "Consulta do servico incorreta");
        esperarFalha(IllegalStateException.class, () -> servico.cadastrar(maria));
        Aluno[] invalidos = {
            null, new Aluno(null, "001", 8), new Aluno(" ", "001", 8),
            new Aluno("Maria", null, 8), new Aluno("Maria", " ", 8),
            new Aluno("Maria", "001", -0.1), new Aluno("Maria", "001", 10.1),
            new Aluno("Maria", "001", Double.NaN),
            new Aluno("Maria", "001", Double.POSITIVE_INFINITY),
            new Aluno("Maria", "001", Double.NEGATIVE_INFINITY)
        };
        for (Aluno invalido : invalidos) {
            esperarFalha(IllegalArgumentException.class, () -> servico.cadastrar(invalido));
            esperarFalha(IllegalArgumentException.class, () -> servico.alterar(invalido));
            verificar(servico.consultar("001") == maria, "Validacao deixou alterar o registro");
            verificar(servico.listar().size() == 1, "Cadastro invalido alterou quantidade");
        }
        servico.alterar(atualizada);
        verificar(servico.consultar("001").getMedia() == 9, "Alteracao do servico incorreta");
        servico.cadastrar(new Aluno("Limite inferior", "002", 0));
        servico.cadastrar(new Aluno("Limite superior", "003", 10));
        verificar(servico.listar().size() == 3, "Limites validos recusados");
        esperarFalha(IllegalStateException.class, () -> servico.consultar("ausente"));
        esperarFalha(IllegalStateException.class, () -> servico.alterar(new Aluno("Ausente", "000", 5)));
        esperarFalha(IllegalStateException.class, () -> servico.excluir("ausente"));
        esperarFalha(IllegalArgumentException.class, () -> servico.consultar(null));
        esperarFalha(IllegalArgumentException.class, () -> servico.excluir(" "));
        servico.excluir("001");
        esperarFalha(IllegalStateException.class, () -> servico.consultar("001"));
        verificar(servico.listar().size() == 2, "Exclusao removeu outros alunos");
        verificar(FabricaServicoAluno.criarEmMemoria().listar().isEmpty(), "Fabrica compartilhou armazem");

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(saida, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        String texto = saida.toString(StandardCharsets.UTF_8);
        verificar(texto.contains("Exclusao inexistente: erro -"), "Tela informou sucesso indevido");
        verificar(!texto.contains("Exclusao inexistente: sucesso"), "Tela informou erro e sucesso");
        verificar(texto.contains("Cadastro com media invalida: erro -"), "Tela nao mostrou validacao");
        verificar(texto.contains("Apos alterar a copia: 2 alunos"), "Demonstracao de copia incorreta");
        System.out.println("OK: " + verificacoes + " verificacoes de CRUD, validacao, "
                + "duplicidade, copia defensiva, falhas e apresentacao.");
    }
}
