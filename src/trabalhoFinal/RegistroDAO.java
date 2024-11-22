package trabalhoFinal;

import trabalhoFinal.ConexaoDB;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {
    public void inserirRegistro(trabalhoFinal.Visitante visitante) {
        String sql = "INSERT INTO visitantes (nome, rg, foto, data_hora_entrada, motivo, apartamento_visitado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, visitante.getNome());
            stmt.setString(2, visitante.getRg());

            if (visitante.getFoto() != null) {
                byte[] fotoBytes = Visitante.imageToBytes(visitante.getFoto());
                stmt.setBytes(3, fotoBytes);
            } else {
                stmt.setNull(3, java.sql.Types.BLOB);  // caso não tenha foto
            }

            stmt.setTimestamp(4, Timestamp.valueOf(visitante.getDataHoraEntrada()));
            stmt.setString(5, visitante.getMotivoVisita());
            stmt.setString(6, visitante.getApartamentoVisitado());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Visitante> listarVisitantes() throws SQLException {
        String sql = "SELECT nome, rg, motivo, apartamento_visitado, data_hora_entrada FROM visitantes WHERE data_hora_saida IS NULL";
        List<Visitante> visitantes = new ArrayList<>();

        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){


            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Visitante visitante = new Visitante();
                visitante.setNome(rs.getString("nome"));
                visitante.setRg(rs.getString("rg"));
                visitante.setMotivoVisita(rs.getString("motivo"));
                visitante.setApartamentoVisitado(rs.getString("apartamento_visitado"));
                visitante.setDataHoraEntrada(rs.getTimestamp("data_hora_entrada").toLocalDateTime());


                visitantes.add(visitante);
            }

            return visitantes;

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("Erro ao listar visitantes: " + e.getMessage());
            throw new SQLException();
        }

    }

    public Visitante buscarVisitantePorRG(String rg) {
        String sql = "SELECT nome, rg, motivo, apartamento_visitado FROM visitantes WHERE rg = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rg);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Visitante visi = new Visitante(rs.getString("nome"),
                        rs.getString("rg"),
                        rs.getString("motivo"),
                        rs.getString("apartamento_visitado"));

                return visi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Visitante> listarHistorico() throws SQLException {
        String sql = "SELECT nome, rg, motivo, apartamento_visitado, data_hora_entrada, data_hora_saida FROM visitantes WHERE data_hora_saida IS NOT NULL";
        List<Visitante> visitantes = new ArrayList<>();

        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){


            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Visitante visitante = new Visitante();
                visitante.setNome(rs.getString("nome"));
                visitante.setRg(rs.getString("rg"));
                visitante.setMotivoVisita(rs.getString("motivo"));
                visitante.setApartamentoVisitado(rs.getString("apartamento_visitado"));
                visitante.setDataHoraEntrada(rs.getTimestamp("data_hora_entrada").toLocalDateTime());
                visitante.setDataHoraSaida(rs.getTimestamp("data_hora_saida").toLocalDateTime());

                visitantes.add(visitante);
            }

            return visitantes;

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("Erro ao listar visitantes: " + e.getMessage());
            throw new SQLException();
        }

    }

    public boolean atualizarVisitante(Visitante visitante) {
        String sql = "UPDATE visitantes SET nome = ?, motivo = ?, apartamento_visitado = ? WHERE rg = ?";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Preencher parâmetros
            stmt.setString(1, visitante.getNome());
            stmt.setString(2, visitante.getMotivoVisita());
            stmt.setString(3, visitante.getApartamentoVisitado());
            stmt.setString(4, visitante.getRg());

            // Log para debug
            System.out.println("Executando SQL: " + sql);
            System.out.println("Parâmetros: ["
                    + visitante.getNome() + ", "
                    + visitante.getMotivoVisita() + ", "
                    + visitante.getApartamentoVisitado() + ", "
                    + visitante.getRg() + "]");

            // Executar atualização
            int rowsUpdated = stmt.executeUpdate();

            // Log de resultado
            System.out.println("Linhas atualizadas: " + rowsUpdated);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar visitante: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }



    public void registrarSaida(String rg, LocalDateTime horarioSaida) {
        String sql = "UPDATE visitantes SET data_hora_saida = ? WHERE rg = ? AND data_hora_saida IS NULL";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Define o valor do horário de saída como um Timestamp
            stmt.setTimestamp(1, Timestamp.valueOf(horarioSaida));

                // Define o RG como parâmetro da consulta
            stmt.setString(2, rg);

                // Executa a atualização
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Nenhum registro encontrado para atualizar ou visitante já registrou a saída.");
            } else {
                System.out.println("Horário de saída registrado com sucesso para o RG: " + rg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao registrar saída: " + e.getMessage());
        }
    }
}

