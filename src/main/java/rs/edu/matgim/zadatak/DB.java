package rs.edu.matgim.zadatak;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\Banka.db";

    public void printFilijala() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Filijala");
            while (rs.next()) {
                int IdFil = rs.getInt("IdFil");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");

                System.out.println(String.format("%d\t%s\t%s", IdFil, Naziv, Adresa));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }

    public void printPositiveRacun() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Racun");// WHERE Stanje >0");
            while (rs.next()) {
                int IdRac = rs.getInt("IdRac");
                String Status = rs.getString("Status");
                String Stanje = rs.getString("Stanje");
                String DozvMinus = rs.getString("DozvMinus");
                String BrojStavki = rs.getString("BrojStavki");
                int IdKom = rs.getInt("IdKom");

                System.out.println(String.format("%d\t%s\t%s\t%s\t%s\t%d", IdRac, Status, Stanje, DozvMinus, BrojStavki, IdKom));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }

    }

    public float zadatak(int idKom, int idRac) {
        float suma = 0;

        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {
            conn.setAutoCommit(false);
            ResultSet rs = s.executeQuery("SELECT IdRac FROM Racun WHERE IdKom=" + idKom);

            while (rs.next()) {
                int idRacZaPraznjenje = rs.getInt("IdRac");
                suma += ugasiRacun(conn, idRacZaPraznjenje);
            }

            if (suma > 0) {
                napraviUplatu(conn, idRac, suma, "Gasenje racuna");
            }

            conn.commit();

            System.out.println("Uspesna realizacija");
            return suma;
        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
            System.out.println("Neuspesna realizacija");
            return 0;
        }

    }

    private int napraviStavku(Connection conn, int idRac, float iznos) throws SQLException {

        try (Statement s = conn.createStatement()) {

            SimpleDateFormat formatterDatum = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatterVreme = new SimpleDateFormat("HH:mm");
            Date date = new Date(System.currentTimeMillis());

            int rb = 1;
            int idSta = 1;
            String datum = formatterDatum.format(date);
            String vreme = formatterVreme.format(date);
            ResultSet rs = s.executeQuery("SELECT MAX(RedBroj)+1 AS RB FROM Stavka WHERE IdRac=" + idRac);
            if (rs.next()) {
                rb = rs.getInt("RB");
            }

            ResultSet rs1 = s.executeQuery("SELECT MAX(idSta)+1 AS ID FROM Stavka");
            if (rs1.next()) {
                idSta = rs.getInt("ID");
            }

            s.execute("INSERT INTO Stavka (IdSta, RedBroj, Datum, Vreme, Iznos) VALUES (" + idSta + "," + rb + ",'" + datum + "','" + vreme + "'," + iznos + ")");

            return idSta;
        }

    }

    private void napraviIsplatu(Connection conn, int idRac, float iznos, float provizija) throws SQLException {

        try (Statement s = conn.createStatement()) {

            int idSta = napraviStavku(conn, idRac, iznos);

            s.execute("INSERT INTO Isplata (IdSta, Provizija) VALUES (" + idSta + "," + provizija + ")");

            s.execute("UPDATE Racun SET Stanje=Stanje-" + iznos + " WHERE IdRac=" + idRac);
        }

    }

    private void napraviUplatu(Connection conn, int idRac, float iznos, String osnov) throws SQLException {

        try (Statement s = conn.createStatement()) {

            int idSta = napraviStavku(conn, idRac, iznos);

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Uplata (IdSta, Osnov) VALUES (?,?)")) {
                ps.setInt(1, idSta);
                ps.setString(2, osnov);
                ps.execute();
            }
            s.execute("UPDATE Racun SET Stanje=Stanje+" + iznos + ", Status = CASE WHEN Stanje+" + iznos + ">-DozvMinus THEN 'A' ELSE Status END WHERE IdRac=" + idRac);
        }

    }

    private float ugasiRacun(Connection conn, int idRac) throws SQLException {
        float rez = 0;
        try (Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT Stanje FROM Racun WHERE IdRac=" + idRac);
            rs.next();
            float stanje = rs.getFloat("Stanje");
            if (stanje > 0) {
                napraviIsplatu(conn, idRac, stanje, 0);
                rez = stanje;
            }
            s.execute("UPDATE Racun SET Status='U' WHERE IdRac=" + idRac);

        }

        return rez;
    }

}
