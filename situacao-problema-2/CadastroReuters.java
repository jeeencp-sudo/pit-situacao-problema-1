import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CadastroReuters {
    private static final Logger LOG = Logger.getLogger(CadastroReuters.class.getName());
    private static final String TITULO = "FAIRFAX SAYS HIGHER TAX HITS FIRST HALF EARNINGS";
    private static final String INSERT =
        "INSERT INTO noticias (id, titulo, fonte, ano) VALUES (?, ?, ?, ?)";
    private static final String SCHEMA =
        "CREATE TABLE noticias (id INTEGER PRIMARY KEY, titulo TEXT NOT NULL "
        + "CHECK(length(trim(titulo)) BETWEEN 1 AND 255), fonte TEXT NOT NULL, "
        + "ano INTEGER NOT NULL CHECK(ano BETWEEN 1 AND 9999)) STRICT";

    public static void main(String[] args) throws SQLException {
        if (args.length == 1 && args[0].equals("--validacao")) {
            System.out.println(cadastrar(null, 7001, TITULO, "abc"));
            System.out.println(cadastrar(null, 7001, " ", "1987"));
            System.out.println(cadastrar(null, 7001, TITULO, "0"));
            return;
        }
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(SCHEMA);
            }
            System.out.println(cadastrar(conn, 7001, TITULO, "abc"));
            System.out.println(cadastrar(conn, 7001, " ", "1987"));
            System.out.println(cadastrar(conn, 7001, TITULO, "0"));
            System.out.println(cadastrar(conn, 7001, TITULO, "1987"));
            System.out.println(cadastrar(conn, 7001, TITULO, "1987"));
            try (Statement stmt = conn.createStatement();
                 var rows = stmt.executeQuery("SELECT COUNT(*) FROM noticias")) {
                rows.next();
                System.out.println("Registros gravados: " + rows.getInt(1));
            }
        }
    }

    private static String cadastrar(Connection conn, int id, String titulo, String anoTexto) {
        String tituloLimpo = titulo == null ? "" : titulo.strip();
        if (tituloLimpo.isEmpty() || tituloLimpo.codePointCount(0, tituloLimpo.length()) > 255) {
            return "Informe um título de 1 a 255 caracteres.";
        }
        if (anoTexto == null || anoTexto.isBlank()) {
            return "Informe o ano.";
        }

        final int ano;
        try {
            ano = Integer.parseInt(anoTexto.strip());
        } catch (NumberFormatException e) {
            return "Informe o ano como um número inteiro.";
        }
        if (ano < 1 || ano > Year.now().getValue()) {
            return "Informe um ano entre 1 e o ano atual.";
        }

        try (PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            stmt.setInt(1, id);
            stmt.setString(2, tituloLimpo);
            stmt.setString(3, "Reuters");
            stmt.setInt(4, ano);
            stmt.executeUpdate();
            return "Notícia cadastrada.";
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Falha ao gravar notícia " + id, e);
            return "Não foi possível concluir o cadastro.";
        }
    }
}
