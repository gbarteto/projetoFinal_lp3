package cnae;

import com.google.gson.Gson;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PesquisaCnae {
    private JLabel lblTitle;
    private JButton btnPesquisa;
    private JTextField iptPesquisa;
    private JLabel lblPesquisa;
    private JTextArea txtResultado;
    private JPanel PesquisaCnae;
    private static final String webService = "https://servicodados.ibge.gov.br/api/v2/cnae/classes/";

    public PesquisaCnae() {
        btnPesquisa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigoCnae = iptPesquisa.getText();
                buscarCnae(codigoCnae);
            }
        });
    }

    private void buscarCnae(String codigoCnae) {
        String codigo = iptPesquisa.getText().trim();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(PesquisaCnae, "Por favor, insira um código CNAE.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Chama a API para buscar o CNAE
            Cnae cnae = CnaeApiClient.buscaCnae(codigo);

            // Formata o resultado para exibir no JTextArea
            StringBuilder resultado = new StringBuilder();
            resultado.append("ID: ").append(cnae.getCodigo()).append("\n");
            resultado.append("Descrição: ").append(cnae.getDescricao()).append("\n");
            resultado.append("Grupo: ").append(cnae.getGrupo().getDescricao()).append("\n");
            resultado.append("Divisão: ").append(cnae.getGrupo().getDivisao().getDescricao()).append("\n");
            resultado.append("Seção: ").append(cnae.getGrupo().getDivisao().getSecao().getDescricao()).append("\n");
            resultado.append("Observações:\n");
            for (String obs : cnae.getObservacoes()) {
                resultado.append("  - ").append(obs).append("\n");
            }

            txtResultado.setText(resultado.toString());
        } catch (Exception ex) {
            txtResultado.setText("Erro ao buscar o CNAE: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public JPanel getPanel(){
            return PesquisaCnae;
    }


}
