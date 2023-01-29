package Rest_Services;



import Domain.Match;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Callable;


public class MatchClient {
    public static final String URL = "http://localhost:8080/rest_services/matches";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) throws Exception {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) {
            throw new Exception(e);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public Match[] findAll() throws Exception {
        return execute(() -> restTemplate.getForObject(URL, Match[].class));
    }

    public Match findOne(Long id) throws Exception {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), Match.class));
    }

    public Match save(Match match) throws Exception {
        return execute(() -> restTemplate.postForObject(URL, match, Match.class));
    }

    public Match update(Match match) throws Exception {
        execute(() -> {
            restTemplate.put(URL, match, Match.class);
            return null;
        });
        return match;
    }

    public void delete(Long id) throws Exception {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }

}

