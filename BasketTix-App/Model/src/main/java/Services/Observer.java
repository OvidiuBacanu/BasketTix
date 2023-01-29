package Services;

import Domain.Match;

import java.util.List;

public interface Observer{
    void update(List<Match> matches) throws Exception;
}