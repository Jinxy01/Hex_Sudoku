package sudokuhex;

import java.sql.*;
import java.util.ArrayList;

public class GestorBD {

    private Connection c = null;
    private Statement stmt = null;

    public GestorBD() {
    }

    public void criaTabelas() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE USER "
                    + "(NOME VARCHAR(50) PRIMARY KEY,"
                    + " PASSWORD VARCHAR(50) NOT NULL)";
            stmt.executeUpdate(sql);

            stmt = c.createStatement();
            sql = "CREATE TABLE DIFICULDADE "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " DESIGNACAO VARCHAR(20))";
            stmt.executeUpdate(sql);

            // Inserir os tipos de dificuldade existentes
            stmt = c.createStatement();
            sql = "INSERT INTO DIFICULDADE (ID,DESIGNACAO) "
                    + " VALUES (1,'Fácil'), (2,'Médio'), (3,'Difícil');";
            stmt.executeUpdate(sql);

            stmt = c.createStatement();
            sql = "CREATE TABLE REGISTO "
                    + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " TEMPO VARCHAR(10),"
                    + " NOMEUSER VARCHAR(50),"
                    + " IDDIFF INTEGER,"
                    + " FOREIGN KEY(NOMEUSER) REFERENCES USER(NOME),"
                    + " FOREIGN KEY(IDDIFF) REFERENCES DIFICULDADE(ID))";

            stmt.executeUpdate(sql);

            stmt.close();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());

        }

    }

    public boolean insereUser(String nome, String pass) throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "INSERT INTO USER "
                    + "(NOME,PASSWORD) "
                    + " VALUES ('" + nome + "', '" + pass + "' );";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
            return true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            c.close();
            return false;
        }
    }

    public boolean insereRegisto(String nome, String tempo, String dificuldade) throws SQLException {

        int idDiff = associaIDDificuldade(dificuldade);
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "INSERT INTO REGISTO "
                    + "(TEMPO,NOMEUSER,IDDIFF) "
                    + " VALUES ('" + tempo + "', '" + nome + "' , " + idDiff + ");";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
            return true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            c.close();
            return false;
        }
    }

    //Associar dificuldade ao ID da tabela
    public int associaIDDificuldade(String dificuldade) throws SQLException {

        int idDiff = -1;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID FROM DIFICULDADE where DESIGNACAO ='" + dificuldade + "';");
            while (rs.next()) {
                idDiff = rs.getInt("ID");
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            c.close();

        }

        return idDiff;
    }

    public ArrayList<Estatistica> devolveRegisto(String dificuldade) throws SQLException {

        ArrayList<Estatistica> alRegistos = new ArrayList<>();

        int idDiff = associaIDDificuldade(dificuldade);

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);

            String sNome, sTempo;
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM REGISTO where IDDIFF = " + idDiff + " "
                    + "ORDER BY TEMPO ASC;");
            while (rs.next()) {
                sNome = rs.getString("NOMEUSER");
                sTempo = rs.getString("TEMPO");
                alRegistos.add(new Estatistica(sTempo, sNome, dificuldade));
            }
            rs.close();
            stmt.close();
            c.close();

            return alRegistos;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            c.close();
        }
        return null;
    }

    public boolean verificaPass(String nome, String pass) throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);

            String passBD = "";
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT PASSWORD FROM USER where NOME='" + nome + "';");
            while (rs.next()) {
                passBD = rs.getString("PASSWORD");
            }
            rs.close();
            stmt.close();
            c.close();
            if (passBD.equals(pass)) {

                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            c.close();
        }
        return false;

    }
//
//    public static void main(String[] args) throws SQLException {
//        GestorBD gera = new GestorBD();
//        gera.criaTabelas();
//        gera.insereUser("Joao", "user");
//        gera.insereUser("Maria", "user");
//        gera.insereRegisto("Joao", "00:13:00", "Médio");
//        gera.insereRegisto("Joao", "00:17:00", "Difícil");
//        gera.insereRegisto("Joao", "00:30:00", "Fácil");
//
//        gera.insereRegisto("Maria", "00:15:25", "Fácil");
//        gera.insereRegisto("Maria", "00:16:15", "Fácil");
//        gera.insereRegisto("Maria", "00:08:00", "Médio");
//        gera.insereRegisto("Maria", "00:14:13", "Difícil");
//        
//        ArrayList<Estatistica> alRegisto = new ArrayList<>();
//        alRegisto = gera.devolveRegisto("Difícil");
//        for (Estatistica estatistica : alRegisto) {
//            System.out.println(estatistica);
//        }
//
//    }

}
