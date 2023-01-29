package Services;

import Domain.Match;
import Domain.User;
import java.util.List;

public interface ServiceInterface {
//     User findUserfromLogin(String name, String password) throws Exception;
     User findUserfromLogin(String name, String password, Observer observer) throws Exception;
     List<Match> getAllMatchesList() throws Exception;
     void sellTicket(String customerName, int nrSeats, Match match) throws Exception;
     List<Match> sortMatchesAfterNrAvailableSeats() throws Exception;
}
