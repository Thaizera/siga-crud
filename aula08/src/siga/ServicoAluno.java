package siga;

import java.util.List;
import java.util.Objects;

/** Regras de domínio compartilhadas pelas operações da aplicação. */
public class ServicoAluno {
    private final AlunoDAO dao;

    public ServicoAluno(AlunoDAO dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    public void cadastrar(Aluno aluno) {
        validar(aluno);
        dao.inserir(aluno);
    }

    public List<Aluno> listar() {
        return dao.listarTodos();
    }

    public Aluno consultar(String matricula) {
        validarMatricula(matricula);
        Aluno aluno = dao.buscarPorMatricula(matricula);
        if (aluno == null) {
            throw new IllegalStateException("Aluno nao encontrado: " + matricula);
        }
        return aluno;
    }

    public void alterar(Aluno aluno) {
        validar(aluno);
        consultar(aluno.getMatricula());
        dao.atualizar(aluno);
    }

    public void excluir(String matricula) {
        consultar(matricula);
        dao.remover(matricula);
    }

    private void validar(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno e obrigatorio.");
        }
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        validarMatricula(aluno.getMatricula());
        if (!Double.isFinite(aluno.getMedia()) || aluno.getMedia() < 0 || aluno.getMedia() > 10) {
            throw new IllegalArgumentException("Media deve ser finita e estar entre 0 e 10.");
        }
    }

    private void validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("Matricula e obrigatoria.");
        }
    }
}
