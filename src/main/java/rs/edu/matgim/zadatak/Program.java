package rs.edu.matgim.zadatak;

public class Program {

    public static void main(String[] args) {

        DB _db = new DB();
        _db.printFilijala();
        _db.printPositiveRacun();
        System.out.println("###################################");
        _db.zadatak(2, 4);
        System.out.println("###################################");
        _db.printPositiveRacun();
    }
}
