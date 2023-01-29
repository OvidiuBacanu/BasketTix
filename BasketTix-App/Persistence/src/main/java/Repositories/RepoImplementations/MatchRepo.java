package Repositories.RepoImplementations;

import Domain.Match;
import Domain.Validator.Validator;
import Repositories.RepoInterfaces.MatchRepoInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class MatchRepo implements MatchRepoInterface {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();
    private Validator<Match> matchValidator;

    public MatchRepo(Properties props, Validator<Match> matchValidator) {
        logger.info("Initializing MatchRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
        this.matchValidator=matchValidator;
    }

    public MatchRepo() { }

    public Validator<Match> getMatchValidator() {
        return matchValidator;
    }

    public void setMatchValidator(Validator<Match> matchValidator) {
        this.matchValidator = matchValidator;
    }

    @Override
    public Match findOne(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Matches where id=? ")) {
            preStmt.setLong(1,aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    String nameTeam1 = result.getString("nameTeam1");
                    String nameTeam2 = result.getString("nameTeam2");
                    double price=result.getDouble("price");
                    int nrAvailableSeats=result.getInt("nrAvailableSeats");

                    Match match = new Match(name,nameTeam1,nameTeam2,price,nrAvailableSeats);
                    match.setId(id);

                    logger.traceExit(match);
                    return match;
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        return null;
    }

    @Override
    public Iterable<Match> findAll() {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();
        List<Match> matches=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Matches")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    String nameTeam1 = result.getString("nameTeam1");
                    String nameTeam2 = result.getString("nameTeam2");
                    double price=result.getDouble("price");
                    int nrAvailableSeats=result.getInt("nrAvailableSeats");


                    Match match = new Match(name,nameTeam1,nameTeam2,price,nrAvailableSeats);
                    match.setId(id);
                    matches.add(match);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        logger.traceExit(matches);
        return matches;
    }

    @Override
    public Match save(Match entity) {
        logger.traceEntry("saving tasks {} ",entity);
        Connection con= dbUtils.getConnection();
        try (PreparedStatement preStmt=con.prepareStatement("insert into Matches (name, nameTeam1, nameTeam2, price, nrAvailableSeats) values(?,?,?,?,?)")){
            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getNameTeam1());
            preStmt.setString(3, entity.getNameTeam2());
            preStmt.setDouble(4,entity.getPrice());
            preStmt.setInt(5,entity.getNrAvailableSeats());
            int result=preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
        }
        catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        return null;
    }

    @Override
    public Match delete(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("delete from Matches where id=?")){
            preStmt.setLong(1,aLong);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Match update(Match entity) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();
        matchValidator.validate(entity);

        try(PreparedStatement preStmt=con.prepareStatement("update Matches set name=?, nameTeam1=?, nameTeam2=?, price=?, nrAvailableSeats=? where id=?")){
            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getNameTeam1());
            preStmt.setString(3, entity.getNameTeam2());
            preStmt.setDouble(4,entity.getPrice());
            preStmt.setInt(5,entity.getNrAvailableSeats());
            preStmt.setLong(6,entity.getId());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public List<Match> sortedMatchesByNrAvailableSeats() {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();
        List<Match> matches=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Matches WHERE nrAvailableSeats>0 order by nrAvailableSeats")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String name = result.getString("name");
                    String nameTeam1 = result.getString("nameTeam1");
                    String nameTeam2 = result.getString("nameTeam2");
                    double price=result.getDouble("price");
                    int nrAvailableSeats=result.getInt("nrAvailableSeats");


                    Match match = new Match(name,nameTeam1,nameTeam2,price,nrAvailableSeats);
                    match.setId(id);
                    matches.add(match);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        logger.traceExit(matches);
        return matches;
    }
}
