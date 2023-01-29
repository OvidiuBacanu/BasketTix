package Domain.Validator;

import Domain.Match;

public class MatchValidator implements Validator<Match>{
    @Override
    public void validate(Match entity) throws ValidationException {
        if(entity.getNrAvailableSeats()<0) {
            throw new ValidationException("Insufficient available seats");
        }
    }
}
