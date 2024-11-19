package trabalhoFinal;

import trabalhoFinal.ConexaoDB;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {
    public void inserirRegistro(trabalhoFinal.Visitante visitante) {
        String sql = "INSERT INTO visitante (nome, rg, foto, data_hora_entrada, motivo_visita, apartamento_visitado, horario_saida) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            stmt.setTimestamp(7, Timestamp.valueOf(visitante.getDataHoraSaida()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Visitante> listarVisitantes() {
        String sql = "SELECT * FROM visitante WHERE data_hora_saida = null";

        return new ArrayList<>();
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

