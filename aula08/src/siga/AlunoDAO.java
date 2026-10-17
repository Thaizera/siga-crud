package siga;

import java.util.List;

/**
 * Interface de acesso a dados de Aluno (padrão DAO), definida na Aula 7.
 * Já está pronta e contempla as quatro operações do CRUD.
 *
 * Repare que ela fala a linguagem do DOMÍNIO: não há termos como tabela,
 * coluna ou INSERT. Quem usa este contrato não precisa saber SQL.
 */
public interface AlunoDAO {

    void inserir(Aluno aluno);                    // Create
    Aluno buscarPorMatricula(String matricula);   // Read (um)
    List<Aluno> listarTodos();                    // Read (vários)
    void atualizar(Aluno aluno);                  // Update
    void remover(String matricula);               // Delete
}
