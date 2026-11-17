import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CatalogoReuters {
    static final Noticia AMOSTRA = new Noticia(
        7001, "FAIRFAX SAYS HIGHER TAX HITS FIRST HALF EARNINGS", "Reuters", 1987);

    record Noticia(int id, String titulo, String fonte, int ano) {}

    interface NoticiaDAO {
        void inserir(Noticia noticia) throws SQLException;
        Optional<Noticia> consultar(int id) throws SQLException;
        List<Noticia> listar() throws SQLException;
        List<Noticia> buscar(String termo) throws SQLException;
        boolean atualizar(Noticia noticia) throws SQLException;
        boolean excluir(int id) throws SQLException;
    }

    static final class NoticiaJdbcDAO implements NoticiaDAO {
        private static final String COLUNAS = "SELECT id, titulo, fonte, ano FROM noticias";
        private final Connection conn;

        NoticiaJdbcDAO(Connection conn) {
            this.conn = conn;
        }

        public void inserir(Noticia n) throws SQLException {
            gravar("INSERT INTO noticias (titulo, fonte, ano, id) VALUES (?, ?, ?, ?)", n);
        }

        public boolean atualizar(Noticia n) throws SQLException {
            return gravar("UPDATE noticias SET titulo=?, fonte=?, ano=? WHERE id=?", n);
        }

        private boolean gravar(String sql, Noticia n) throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, n.titulo());
                stmt.setString(2, n.fonte());
                stmt.setInt(3, n.ano());
                stmt.setInt(4, n.id());
                return stmt.executeUpdate() == 1;
            }
        }

        public Optional<Noticia> consultar(int id) throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement(COLUNAS + " WHERE id=?")) {
                stmt.setInt(1, id);
                return ler(stmt).stream().findFirst();
            }
        }

        public List<Noticia> listar() throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement(COLUNAS + " ORDER BY id")) {
                return ler(stmt);
            }
        }

        public List<Noticia> buscar(String termo) throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement(
                    COLUNAS + " WHERE titulo LIKE ? OR fonte LIKE ? ORDER BY id")) {
                String padrao = "%" + termo + "%";
                stmt.setString(1, padrao);
                stmt.setString(2, padrao);
                return ler(stmt);
            }
        }

        public boolean excluir(int id) throws SQLException {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM noticias WHERE id=?")) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() == 1;
            }
        }

        private List<Noticia> ler(PreparedStatement stmt) throws SQLException {
            List<Noticia> noticias = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    noticias.add(new Noticia(rs.getInt("id"), rs.getString("titulo"),
                        rs.getString("fonte"), rs.getInt("ano")));
                }
            }
            return noticias;
        }
    }

    static final class NoticiaService {
        private final NoticiaDAO dao;

        NoticiaService(NoticiaDAO dao) {
            this.dao = dao;
        }

        void cadastrar(Noticia noticia) throws SQLException {
            validar(noticia);
            dao.inserir(noticia);
        }

        boolean editar(Noticia noticia) throws SQLException {
            validar(noticia);
            return dao.atualizar(noticia);
        }

        Optional<Noticia> consultar(int id) throws SQLException {
            return dao.consultar(id);
        }

        List<Noticia> listar() throws SQLException {
            return dao.listar();
        }

        List<Noticia> buscar(String termo) throws SQLException {
            if (termo == null || termo.isBlank()) {
                throw new IllegalArgumentException("Informe um termo de busca.");
            }
            return dao.buscar(termo.strip());
        }

        boolean excluir(int id) throws SQLException {
            return dao.excluir(id);
        }

        private void validar(Noticia noticia) {
            if (noticia.titulo() == null || noticia.titulo().isBlank()) {
                throw new IllegalArgumentException("Informe o título.");
            }
            if (noticia.ano() < 1 || noticia.ano() > Year.now().getValue()) {
                throw new IllegalArgumentException("Ano fora da faixa permitida.");
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        if (args.length == 1 && args[0].equals("--validacao")) {
            verificarServico();
            return;
        }
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE noticias (id INTEGER PRIMARY KEY, "
                    + "titulo TEXT NOT NULL, fonte TEXT NOT NULL, ano INTEGER NOT NULL)");
            }
            NoticiaService service = new NoticiaService(new NoticiaJdbcDAO(conn));
            service.cadastrar(AMOSTRA);
            System.out.println("Listagem: " + service.listar().size());
            System.out.println("Busca FAIRFAX: " + service.buscar("FAIRFAX").size());
            System.out.println("Busca com entrada SQL: " + service.buscar("' OR 1=1 --").size());
            System.out.println("Consulta: " + service.consultar(AMOSTRA.id()).orElseThrow());
            System.out.println("Edição: " + service.editar(
                new Noticia(AMOSTRA.id(), AMOSTRA.titulo(), "Reuters News", AMOSTRA.ano())));
            System.out.println("Fonte após edição: " + service.consultar(AMOSTRA.id()).orElseThrow().fonte());
            System.out.println("Exclusão: " + service.excluir(AMOSTRA.id()));
            System.out.println("Registros restantes: " + service.listar().size());
        }
    }

    private static void verificarServico() throws SQLException {
        List<Noticia> gravacoes = new ArrayList<>();
        NoticiaDAO dao = new NoticiaDAO() {
            public void inserir(Noticia noticia) { gravacoes.add(noticia); }
            public Optional<Noticia> consultar(int id) { throw new UnsupportedOperationException(); }
            public List<Noticia> listar() { throw new UnsupportedOperationException(); }
            public List<Noticia> buscar(String termo) { throw new UnsupportedOperationException(); }
            public boolean atualizar(Noticia noticia) { throw new UnsupportedOperationException(); }
            public boolean excluir(int id) { throw new UnsupportedOperationException(); }
        };
        NoticiaService service = new NoticiaService(dao);
        try {
            service.cadastrar(new Noticia(7001, AMOSTRA.titulo(), "Reuters", 0));
            throw new AssertionError("O serviço aceitou um ano inválido.");
        } catch (IllegalArgumentException e) {
            if (!gravacoes.isEmpty()) {
                throw new AssertionError("O serviço chamou o DAO com dados inválidos.");
            }
            System.out.println(e.getMessage() + " Chamadas de gravação: 0.");
        }
        service.cadastrar(AMOSTRA);
        if (!gravacoes.equals(List.of(AMOSTRA))) {
            throw new AssertionError("O serviço não encaminhou o registro válido ao DAO.");
        }
        System.out.println("Amostra válida. Chamadas de gravação: 1.");
    }
}
