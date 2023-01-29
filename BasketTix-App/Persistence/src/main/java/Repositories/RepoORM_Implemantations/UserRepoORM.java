package Repositories.RepoORM_Implemantations;

import Domain.User;
import Repositories.RepoImplementations.JdbcUtils;
import Repositories.RepoInterfaces.UserRepoInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class UserRepoORM implements UserRepoInterface {

    private static final Logger logger= LogManager.getLogger();
    private SessionFactory sessionFactory;

    public UserRepoORM(SessionFactory sessionFactory) {
        this.sessionFactory=sessionFactory;
        logger.info("Initializing MatchRepo with properties: {} ",sessionFactory);
    }

    @Override
    public User findOne(Long aLong) {
        try (Session session = sessionFactory.openSession()) {
                Transaction tx=null;
                try{
                    tx = session.beginTransaction();
                    User user= session.createQuery(" from User where id=:id", User.class)
                            .setLong("id",aLong)
                            .setMaxResults(1)
                            .uniqueResult();
                    tx.commit();
                    return user;
                } catch(RuntimeException ex){
                    if (tx!=null)
                        tx.rollback();
                }
            }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                List<User> list = session.createQuery("from User", User.class).list();
                tx.commit();
                return list;
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public User save(User entity) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                session.save(entity);
                tx.commit();
                return entity;
            }catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public User delete(Long aLong) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                User user= session.createQuery("from User where id=:id", User.class)
                        .setLong("id",aLong)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(user);
                tx.commit();
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public User update(User entity) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                User user = (User) session.load( User.class, entity.getId());
                user.setUsername(entity.getUsername());
                user.setPassword(entity.getPassword());
                tx.commit();
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return entity;
    }

    @Override
    public User findUserAfterUsernamePassword(String name, String password) {
        try(Session session = sessionFactory.openSession()){
            Transaction tx=null;
            try{
                tx = session.beginTransaction();
                User user= session.createQuery("from User where password like :pass and username like :user", User.class)
                        .setParameter("pass", password)
                        .setParameter("user", name)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                return user;
            } catch(RuntimeException ex){
                if (tx!=null)
                    tx.rollback();
            }
        }
        return null;
    }
}

