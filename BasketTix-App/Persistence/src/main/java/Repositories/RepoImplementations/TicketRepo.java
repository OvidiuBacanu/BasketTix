package Repositories.RepoImplementations;

import Domain.Match;
import Domain.Ticket;
import Repositories.RepoInterfaces.MatchRepoInterface;
import Repositories.RepoInterfaces.TicketRepoInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TicketRepo implements TicketRepoInterface {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();


    private MatchRepoInterface matchRepo;

    public TicketRepo(Properties props,MatchRepoInterface matchRepo) {
        logger.info("Initializing MatchRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
        this.matchRepo=matchRepo;
    }

    @Override
    public Ticket findOne(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Tickets where id=?")) {
            preStmt.setLong(1,aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String customerName = result.getString("customerName");
                    int nrSeats=result.getInt("nrSeats");
                    Match match= matchRepo.findOne(result.getLong("match"));

                    Ticket ticket=new Ticket(customerName,nrSeats,match);
                    ticket.setId(id);

                    logger.traceExit(match);
                    return ticket;
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
    public Iterable<Ticket> findAll() {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();
        List<Ticket> tickets=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Tickets")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String customerName = result.getString("customerName");
                    int nrSeats=result.getInt("nrSeats");
                    Match match= matchRepo.findOne(result.getLong("match"));

                    Ticket ticket=new Ticket(customerName,nrSeats,match);
                    ticket.setId(id);
                    tickets.add(ticket);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        logger.traceExit(tickets);
        return tickets;
    }

    @Override
    public Ticket save(Ticket entity) {
        logger.traceEntry("saving tasks {} ",entity);
        Connection con= dbUtils.getConnection();
        try (PreparedStatement preStmt=con.prepareStatement("insert into Tickets (customerName, nrSeats, match) values(?,?,?)")){
            preStmt.setString(1, entity.getCustomerName());
            preStmt.setInt(2, entity.getNrSeats());
            preStmt.setLong(3, entity.getMatch().getId());
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
    public Ticket delete(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("delete from Tickets where id=?")){
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
    public Ticket update(Ticket entity) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("update Tickets set customerName=?, nrSeats=?, match=? where id=?")){
            preStmt.setString(1, entity.getCustomerName());
            preStmt.setInt(2,entity.getNrSeats());
            preStmt.setLong(3,entity.getMatch().getId());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
        return null;
    }
}
