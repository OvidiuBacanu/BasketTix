package dto;

import Domain.Match;

import java.io.Serializable;

public class TicketDTO implements Serializable {
    private String customerName;
    private int nrSeats;
    private Match match;

    public TicketDTO(String customerName, int nrSeats, Match match) {
        this.customerName = customerName;
        this.nrSeats = nrSeats;
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

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
