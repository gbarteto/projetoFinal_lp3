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


    public List<Visitante> listarVisitantes() throws SQLException {
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
            System.out.println("Erro ao registrar saída: " + e.getMessage());
            throw new SQLException();
        }

    }

    public Visitante buscarVisitantePorRG(String rg) {
        String sql = "SELECT nome, rg, motivo_visita, apartamento FROM tabela_visitantes WHERE rg = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rg);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Visitante(
                        rs.getString("nome"),
                        rs.getString("rg"),
                        rs.getString("motivo_visita"),
                        rs.getString("apartamento")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean atualizarVisitante(Visitante visitante) {
        String sql = "UPDATE visitantes SET nome = ?, motivo = ?, apartamento_visitado = ? WHERE rg = ?";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, visitante.getNome());
            stmt.setString(2, visitante.getMotivoVisita());
            stmt.setString(3, visitante.getApartamentoVisitado());
            stmt.setString(4, visitante.getRg());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void registrarSaida(String rg, LocalDateTime horarioSaida) {
        String sql = "UPDATE visitante SET horario_saida ="+ horarioSaida +"WHERE rg =" + rg + " AND horario_saida IS NULL";

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

