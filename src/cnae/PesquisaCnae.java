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
    private JTextArea textArea1;
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
        try {
            String urlParaChamada = webService + codigoCnae;
            URL url = new URL(urlParaChamada);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

            if (conexao.getResponseCode() != HttpURLConnection.HTTP_OK) {
                textArea1.setText("Erro na requisição: Código " + conexao.getResponseCode());
                return;
            }

            BufferedReader resposta = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            String jsonEmString = converteJsonEmString(resposta);

            // Desserializa o JSON em um objeto Cnae
            Gson gson = new Gson();
            Cnae cnae = gson.fromJson(jsonEmString, Cnae.class);

            // Exibe o resultado na área de texto
            textArea1.setText("CNAE: " + cnae.getCodigo() + "\n" +
                    "Descrição: " + cnae.getDescricao());

        } catch (Exception e) {
            textArea1.setText("Erro: " + e.getMessage());
        }
    }

    private String converteJsonEmString(BufferedReader resposta) throws Exception {
        StringBuilder sb = new StringBuilder();
        String linha;
        while ((linha = resposta.readLine()) != null) {
            sb.append(linha);
        }
        return sb.toString();
    }


}
