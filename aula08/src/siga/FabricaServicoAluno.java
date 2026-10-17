package siga;

/** Fábrica simples que centraliza a montagem da aplicação em memória. */
public final class FabricaServicoAluno {
    private FabricaServicoAluno() {
    }

    public static ServicoAluno criarEmMemoria() {
        return new ServicoAluno(new AlunoDAOMemoria());
    }
}
