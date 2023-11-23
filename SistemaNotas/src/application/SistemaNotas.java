package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SistemaNotas {

	private static final String URL = "jdbc:mysql://localhost/seu_banco_de_dados";
	private static final String USUARIO = "user";
	private static final String SENHA = "password";

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		try {
			// Conecte-se ao banco de dados
			Connection connection = DriverManager.getConnection(URL, USUARIO, SENHA);
			Integer opcao;
			do {
				menu();
				System.out.print("Digite uma opção: ");
				
				try {
					String input = console.nextLine();
					opcao = Integer.parseInt(input);
				
				switch (opcao) {
				case 0:
					System.out.println("Sistema fechado!");
					// Feche a conexão com o banco de dados
					connection.close();
					break;
				case 1:
					System.out.print("Digite o nome do aluno: ");
					String nomeAluno1 = console.nextLine();
					cadastrarAluno(connection, nomeAluno1);
					break;
				case 2:
					System.out.print("Digite o nome do aluno: ");
					String nomeAluno2 = console.nextLine();
					System.out.print("Digite a disciplina: ");
					String disciplina = console.nextLine();
					System.out.print("Digite a nota: ");
					double nota = console.nextDouble();
					// Insira uma nova nota
					inserirNota(connection, nomeAluno2, disciplina, nota);
					break;
				case 3:
					System.out.print("Digite o nome do aluno: ");
					String nomeAluno3 = console.nextLine();
					// Visualize as notas de um aluno
					visualizarNotasAluno(connection, nomeAluno3);
					break;
				case 4:
					System.out.print("Digite o nome do aluno: ");
					String nomeAluno4 = console.nextLine();
					System.out.print("Digite a disciplina: ");
					String disciplina4 = console.nextLine();
					System.out.print("Digite a nota: ");
					double nota4 = console.nextDouble();
					// Atualize uma nota
					atualizarNota(connection, nomeAluno4, disciplina4, nota4);
					break;
				default:
					System.out.println("Opção inválida, escolha novamente.");
					break;
				}
				
				} catch (NumberFormatException e) {
					System.out.println("Opção inválida, escolha novamente.");
					opcao = -1;
					console.nextLine();
				} catch (InputMismatchException e) {
					System.out.println("Opção inválida, escolha novamente.");
					opcao = -1;
					console.nextLine();
				}
			} while (opcao != 0);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			console.close();
		}
	}

	private static void menu() {
		System.out.println("---MENU---");
		System.out.println("0) SAIR");
		System.out.println("1) Cadastrar aluno");
		System.out.println("2) Inserir nota");
		System.out.println("3) Visualizar notas");
		System.out.println("4) Atualizar nota");
	}

	private static void cadastrarAluno(Connection connection, String nomeAluno) throws SQLException {
		String insertQuery = "INSERT INTO alunos (nome) VALUES (?)";
		PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
		preparedStatement.setString(1, nomeAluno);
		preparedStatement.executeUpdate();
		System.out.println("Aluno cadastrado com sucesso.");
	}

	private static void inserirNota(Connection connection, String nomeAluno, String disciplina, double nota)
			throws SQLException {
		String insertQuery = "INSERT INTO Notas (aluno_id, disciplina, nota) "
				+ "VALUES ((SELECT id FROM Alunos WHERE nome = ?), ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
		preparedStatement.setString(1, nomeAluno);
		preparedStatement.setString(2, disciplina);
		preparedStatement.setDouble(3, nota);
		preparedStatement.executeUpdate();
		System.out.println("Nota inserida com sucesso.");
	}

	private static void visualizarNotasAluno(Connection connection, String nomeAluno) throws SQLException {
		String selectQuery = "SELECT disciplina, nota FROM Notas "
				+ "WHERE aluno_id = (SELECT id FROM Alunos WHERE nome = ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
		preparedStatement.setString(1, nomeAluno);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			String disciplina = resultSet.getString("disciplina");
			double nota = resultSet.getDouble("nota");
			System.out.println("Disciplina: " + disciplina + ", Nota: " + nota);
		}
	}

	private static void atualizarNota(Connection connection, String nomeAluno, String disciplina, double novaNota)
			throws SQLException {
		String updateQuery = "UPDATE Notas " + "SET nota = ? "
				+ "WHERE aluno_id = (SELECT id FROM Alunos WHERE nome = ?) " + "AND disciplina = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
		preparedStatement.setDouble(1, novaNota);
		preparedStatement.setString(2, nomeAluno);
		preparedStatement.setString(3, disciplina);
		int rowsUpdated = preparedStatement.executeUpdate();
		if (rowsUpdated > 0) {
			System.out.println("Nota atualizada com sucesso.");
		} else {
			System.out.println("Nota não encontrada para atualização.");
		}
	}
}