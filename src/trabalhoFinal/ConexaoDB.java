package trabalhoFinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    private static final String url = "jdbc:mysql://localhost:3306/nome_do_banco";
    private static final String user = "root";
    private static final String senha = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, senha);
    }
}

