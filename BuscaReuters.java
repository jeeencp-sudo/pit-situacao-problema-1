import java.sql.*;

public class BuscaReuters {
    private static final String BUSCA =
        "SELECT id FROM noticias WHERE titulo LIKE ? OR fonte LIKE ?";

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement setup = conn.createStatement()) {
            setup.executeUpdate("CREATE TABLE noticias (id INTEGER PRIMARY KEY, "
                + "titulo TEXT NOT NULL, fonte TEXT NOT NULL)");
            setup.executeUpdate("INSERT INTO noticias VALUES "
                + "(7001, 'FAIRFAX SAYS HIGHER TAX HITS FIRST HALF EARNINGS', 'Reuters')");

            String teste = "' OR 1=1 --";
            System.out.println("Título: " + buscar(conn, "FAIRFAX"));
            System.out.println("Fonte: " + buscar(conn, "Reuters"));
            try (ResultSet rs = setup.executeQuery(
                    "SELECT id FROM noticias WHERE titulo LIKE '%" + teste + "%'"
                    + " OR fonte LIKE '%" + teste + "%'")) {
                System.out.println("Concatenada (controle inseguro): " + contar(rs));
            }
            System.out.println("Parametrizada: " + buscar(conn, teste));
        }
    }

    private static int buscar(Connection conn, String termo) throws SQLException {
        String padrao = "%" + termo + "%";
        try (PreparedStatement stmt = conn.prepareStatement(BUSCA)) {
            stmt.setString(1, padrao);
            stmt.setString(2, padrao);
            try (ResultSet rs = stmt.executeQuery()) {
                return contar(rs);
            }
        }
    }

    private static int contar(ResultSet rs) throws SQLException {
        int total = 0;
        while (rs.next()) {
            total++;
        }
        return total;
    }
}
