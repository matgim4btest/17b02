package rs.edu.matgim.zadatak;

import java.sql.*;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\Banka.db";

    public void printFilijala() {
        try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

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
    
    void printPositiveRacun()
    {
                try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Racun WHERE Stanje >0");
            while (rs.next()) {
                int IdRac = rs.getInt("IdRac");
                String Status = rs.getString("Status");
                String Stanje = rs.getString("Stanje");
                String DozvMinus = rs.getString("DozvMinus");
                String BrojStavki = rs.getString("BrojStavki");

                System.out.println(String.format("%d\t%s\t%s", IdRac, Status, Stanje,DozvMinus,BrojStavki));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }

    }
    
    float zadatak(int idKom, int idRac) throws SQLException
    {
        try( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()){
            String upit1="SELECT  SUM(Stanje) FROM Racun WHERE IdKom=? AND Stanje>0";
            PreparedStatement st = connection.prepareStatement(upit);
            conn.setAutoCommit(false);
            t1.setInt(1,IdKom);
          ResultSet rs=st1.executeQuery();
          String upit2="SELECT  IDrac,Stanje FROM Racun WHERE IdKom=? AND Stanje>0";
          PreparedStatement st2=connection.prepareStatement(upit2);
        rs=st2.executeQuery();
           
            //st.execute();
            
            if(false) throw new SQLException("nestala struja");
            
            //st.setInt(1,suma);
            //st.setInt(2,toRac);
            st.execute();
            conn.commit();
            connection.setAutoCommit(true);            
        }catch(SQLException e) {connection.rollback();System.out.println("greska u transakciji");}
    }

    
}
