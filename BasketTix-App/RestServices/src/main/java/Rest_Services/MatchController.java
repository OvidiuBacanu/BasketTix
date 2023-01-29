package Rest_Services;

import Domain.Match;
import Repositories.RepoImplementations.MatchRepo;
import Repositories.RepoImplementations.UserRepo;
import Repositories.RepoInterfaces.MatchRepoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest_services/matches")
public class MatchController {
    private static final String template = "Hello, %s!";

    ApplicationContext context = new ClassPathXmlApplicationContext("ConfigPage.xml");
    private MatchRepoInterface matchRepo=context.getBean(MatchRepo.class);

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }

    @RequestMapping( method= RequestMethod.GET)
    public Iterable<Match> findAll(){
        return matchRepo.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> findOne(@PathVariable Long id){

        Match match=matchRepo.findOne(id);
        if (match==null)
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Match>(match, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Match save(@RequestBody Match match){
        matchRepo.save(match);
        match.setId(0L);
        return match;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Match update(@RequestBody Match match) {
        matchRepo.update(match);
        return match;
    }

    @RequestMapping(value = "/{id}", method= RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id){
        Match match=matchRepo.delete(id);
        if (match!=null)
            return new ResponseEntity<String>("Match not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Match>(match, HttpStatus.OK);
    }

    @RequestMapping("/sorted")
    public Iterable<Match> findAllSorted(){
        return matchRepo.sortedMatchesByNrAvailableSeats();
    }
}
