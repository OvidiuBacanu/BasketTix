package Repositories.RepoInterfaces;

import Domain.Match;

import java.util.List;

public interface MatchRepoInterface extends Repository<Long, Match> {
    List<Match> sortedMatchesByNrAvailableSeats();
}
