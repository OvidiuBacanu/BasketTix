package Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Match extends Entity<Long> {
    private String name;
    private String nameTeam1;
    private String nameTeam2;
    private double price;
    private int nrAvailableSeats;

    public Match(String name, String nameTeam1, String nameTeam2, double price, int nrAvailableSeats) {
        this.name = name;
        this.nameTeam1 = nameTeam1;
        this.nameTeam2 = nameTeam2;
        this.price = price;
        this.nrAvailableSeats = nrAvailableSeats;
    }

    public Match() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameTeam1() {
        return nameTeam1;
    }

    public void setNameTeam1(String nameTeam1) {
        this.nameTeam1 = nameTeam1;
    }

    public String getNameTeam2() {
        return nameTeam2;
    }

    public void setNameTeam2(String nameTeam2) {
        this.nameTeam2 = nameTeam2;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNrAvailableSeats() {
        return nrAvailableSeats;
    }

    public void setNrAvailableSeats(int nrAvailableSeats) {
        this.nrAvailableSeats = nrAvailableSeats;
    }

    /*@Override
    public String toString() {
        return name+", "+nameTeam1+", "+nameTeam2+", "+String.valueOf(price)+", "+nrAvailableSeats;
    }*/

    @Override
    public String toString() {
        return "Match{" +
                "name='" + name + '\'' +
                ", nameTeam1='" + nameTeam1 + '\'' +
                ", nameTeam2='" + nameTeam2 + '\'' +
                ", price=" + price +
                ", nrAvailableSeats=" + nrAvailableSeats +
                '}';
    }

    @JsonIgnore
    public String getNrAvailableSeatsAsString() {
        if(nrAvailableSeats>0)
            return String.valueOf(nrAvailableSeats);
        else
            return "SOLD OUT";
    }
}
