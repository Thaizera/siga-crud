package siga;

import java.util.List;

/** Apresentação: chama o serviço e traduz falhas em mensagens específicas. */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SIGA - Aula 8: CRUD completo ===");
        ServicoAluno servico = FabricaServicoAluno.criarEmMemoria();
        executar("Cadastro de Maria", () -> servico.cadastrar(new Aluno("Maria Silva", "2026001", 8.5)));
        executar("Cadastro de Joao", () -> servico.cadastrar(new Aluno("Joao Souza", "2026002", 6.0)));
        System.out.println("\nAlunos cadastrados:");
        servico.listar().forEach(System.out::println);
        executar("Consulta", () -> System.out.println(servico.consultar("2026001")));
        executar("Atualizacao", () -> servico.alterar(new Aluno("Maria Silva", "2026001", 9.0)));
        executar("Consulta apos atualizacao", () -> System.out.println(servico.consultar("2026001")));

        List<Aluno> copia = servico.listar();
        copia.add(new Aluno("Intruso Silva", "9999999", 10));
        copia.clear();
        System.out.println("\nApos alterar a copia: " + servico.listar().size() + " alunos no servico.");

        executar("Cadastro duplicado", () -> servico.cadastrar(new Aluno("Outra Maria", "2026001", 7)));
        executar("Cadastro com media invalida", () -> servico.cadastrar(new Aluno("Media invalida", "2026003", 50)));
        executar("Atualizacao com nome vazio", () -> servico.alterar(new Aluno(" ", "2026001", 8)));
        executar("Atualizacao inexistente", () -> servico.alterar(new Aluno("Ausente", "0000000", 8)));
        executar("Exclusao inexistente", () -> servico.excluir("0000000"));
        executar("Exclusao de Joao", () -> servico.excluir("2026002"));
        executar("Consulta de Joao excluido", () -> System.out.println(servico.consultar("2026002")));
        System.out.println("\nLista final:");
        servico.listar().forEach(System.out::println);
    }

    private static void executar(String operacao, Runnable acao) {
        try {
            acao.run();
            System.out.println(operacao + ": sucesso.");
        } catch (IllegalArgumentException | IllegalStateException erro) {
            System.out.println(operacao + ": erro - " + erro.getMessage());
        }
    }
}
