package Domain;

public class Ticket extends Entity<Long> {
    private String customerName;
    private int nrSeats;
    private Match match;

    public Ticket(String customerName, int nrSeats, Match match) {
        this.customerName = customerName;
        this.nrSeats = nrSeats;
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getNrSeats() {
        return nrSeats;
    }

    public void setNrSeats(int nrSeats) {
        this.nrSeats = nrSeats;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "customerName='" + customerName + '\'' +
                ", nrSeats=" + nrSeats +
                ", match=" + match +
                '}';
    }
}
