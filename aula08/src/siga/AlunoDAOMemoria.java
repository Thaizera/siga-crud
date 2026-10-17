package siga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Armazenamento em memória; os dados duram apenas durante a execução. */
public class AlunoDAOMemoria implements AlunoDAO {
    private final List<Aluno> armazem = new ArrayList<>();

    @Override
    public void inserir(Aluno aluno) {
        Objects.requireNonNull(aluno);
        Objects.requireNonNull(aluno.getMatricula());
        if (buscarPorMatricula(aluno.getMatricula()) != null) {
            throw new IllegalStateException("Matricula ja cadastrada: " + aluno.getMatricula());
        }
        armazem.add(aluno);
    }

    @Override
    public Aluno buscarPorMatricula(String matricula) {
        for (Aluno aluno : armazem) {
            if (aluno.getMatricula().equals(matricula)) {
                return aluno;
            }
        }
        return null;
    }

    @Override
    public List<Aluno> listarTodos() {
        return new ArrayList<>(armazem);
    }

    @Override
    public void atualizar(Aluno aluno) {
        Objects.requireNonNull(aluno);
        for (int indice = 0; indice < armazem.size(); indice++) {
            if (armazem.get(indice).getMatricula().equals(aluno.getMatricula())) {
                armazem.set(indice, aluno);
                return;
            }
        }
        throw new IllegalStateException("Aluno nao encontrado: " + aluno.getMatricula());
    }

    @Override
    public void remover(String matricula) {
        Aluno encontrado = buscarPorMatricula(matricula);
        if (encontrado == null) {
            throw new IllegalStateException("Aluno nao encontrado: " + matricula);
        }
        armazem.remove(encontrado);
    }
}
