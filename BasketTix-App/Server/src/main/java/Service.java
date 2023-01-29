import Domain.Match;
import Domain.Ticket;
import Domain.User;
import Repositories.RepoInterfaces.MatchRepoInterface;
import Repositories.RepoInterfaces.TicketRepoInterface;
import Repositories.RepoInterfaces.UserRepoInterface;
import Services.Observable;
import Services.Observer;
import Services.ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements ServiceInterface, Observable {
    private UserRepoInterface userRepo;
    private MatchRepoInterface matchRepo;
    private TicketRepoInterface ticketRepo;

    public Service(UserRepoInterface userRepo, MatchRepoInterface matchRepo, TicketRepoInterface ticketRepo) {
        this.userRepo = userRepo;   
        this.matchRepo = matchRepo;
        this.ticketRepo = ticketRepo;
    }

//    public synchronized User findUserfromLogin(String name, String password){
//        return userRepo.findUserAfterUsernamePassword(name,password);
//    }

    public synchronized User findUserfromLogin(String name, String password, Observer observer){
        User user=userRepo.findUserAfterUsernamePassword(name,password);
        if(user!=null){
            if(observers.get(user.getId())==null)
                observers.put(user.getId(), observer);

            System.out.println("AICI");
            System.out.println(observers);
        }
        return user;
    }

    public synchronized List<Match> getAllMatchesList(){
        return StreamSupport.stream(matchRepo.findAll().spliterator(),false).collect(Collectors.toList());
    }

    public synchronized void sellTicket(String customerName, int nrSeats, Match match){
        match.setNrAvailableSeats(match.getNrAvailableSeats()-nrSeats);
        matchRepo.update(match);
        ticketRepo.save(new Ticket(customerName,nrSeats,match));
        notifyObservers();
    }

    public synchronized List<Match> sortMatchesAfterNrAvailableSeats() {
        return matchRepo.sortedMatchesByNrAvailableSeats();
    }

    private Map<Long,Observer> observers=new ConcurrentHashMap<>();
    @Override
    public void addObserver(Observer e) {}

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        ExecutorService executor= Executors.newFixedThreadPool(5);
        List<Match> rez=getAllMatchesList();
        for(Observer x: observers.values()){
            executor.execute(() -> {
                try {
                    System.out.println("Notifying observers");
                    x.update(rez);
                } catch (Exception e) {
                    System.out.println("Error notifying friend " + e);
                }
            });
        }
        executor.shutdown();
    }
}
