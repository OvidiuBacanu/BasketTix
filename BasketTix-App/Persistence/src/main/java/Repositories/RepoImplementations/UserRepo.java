package Repositories.RepoImplementations;

import Domain.User;
import Repositories.RepoInterfaces.UserRepoInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserRepo implements UserRepoInterface {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public UserRepo(Properties props) {
        logger.info("Initializing MatchRepo with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public User findOne(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Users where id=?")) {
            preStmt.setLong(1,aLong);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");

                    User user = new User(username,password);
                    user.setId(id);

                    logger.traceExit(user);
                    return user;
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
    public Iterable<User> findAll() {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();
        List<User> users=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Users")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String username = result.getString("username");
                    String password = result.getString("password");

                    User user = new User(username,password);
                    user.setId(id);
                    users.add(user);
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        logger.traceExit(users);
        return users;
    }

    @Override
    public User save(User entity) {
        logger.traceEntry("saving tasks {} ",entity);
        Connection con= dbUtils.getConnection();
        try (PreparedStatement preStmt=con.prepareStatement("insert into Users (username, password) values(?,?)")){
            preStmt.setString(1, entity.getUsername());
            preStmt.setString(2, entity.getPassword());
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
    public User delete(Long aLong) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("delete from Users where id=?")){
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
    public User update(User entity) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("update Matches set name=?, username=? where id=?")){
            preStmt.setString(1, entity.getUsername());
            preStmt.setString(2, entity.getPassword());
            preStmt.setLong(3,entity.getId());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public User findUserAfterUsernamePassword(String name, String password) {
        logger.traceEntry();
        Connection con= dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Users where username=? and password=?")) {
            preStmt.setString(1,name);
            preStmt.setString(2,password);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");

                    User user = new User(name,password);
                    user.setId(id);

                    logger.traceExit(user);
                    return user;
                }
            }
        }
        catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB: "+ex);
        }
        return null;
    }
}
