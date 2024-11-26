package cnae;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CnaeApiClient {
    private static final String webService = "https://servicodados.ibge.gov.br/api/v2/cnae/classes/";

    public static Cnae buscaCnae(String cod) throws Exception {
        String urlParaChamada = webService + cod;

        try {
            URL url = new URL(urlParaChamada);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

            if (conexao.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new RuntimeException("Erro HTTP: " + conexao.getResponseCode());

            BufferedReader resposta = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String linha;

            while ((linha = resposta.readLine()) != null) {
                jsonBuilder.append(linha);
            }

            Gson gson = new Gson();
            return gson.fromJson(jsonBuilder.toString(), Cnae.class);

        } catch (Exception e) {
            throw new Exception("Erro ao buscar CNAE: " + e.getMessage(), e);
        }
    }
}
