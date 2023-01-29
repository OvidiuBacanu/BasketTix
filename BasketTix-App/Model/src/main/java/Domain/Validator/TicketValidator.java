package Domain.Validator;

import Domain.Ticket;

public class TicketValidator implements Validator<Ticket>{
    @Override
    public void validate(Ticket entity) throws ValidationException {
        if(entity.getNrSeats()<0) {
            throw new ValidationException("Invalid seats number");
        }
    }
}
