
import Domain.Match;
import Domain.User;
import Domain.Validator.MatchValidator;
import Domain.Validator.Validator;
import Repositories.RepoImplementations.MatchRepo;
import Repositories.RepoImplementations.TicketRepo;
import Repositories.RepoImplementations.UserRepo;
import Repositories.RepoInterfaces.MatchRepoInterface;
import Repositories.RepoInterfaces.TicketRepoInterface;
import Repositories.RepoInterfaces.UserRepoInterface;
import Repositories.RepoORM_Implemantations.UserRepoORM;
import Services.ServiceInterface;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import utils.AbstractServer;
import utils.RpcConcurrentServer;
import utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    static SessionFactory sessionFactory;

    static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
            System.out.println("Sesiune creeata");
        }
        catch (Exception e) {
            System.err.println("Exception "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close(){
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }
    public static void main(String[] args) {

        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }
        initialize();

        //UserRepoInterface userRepo=new UserRepo(serverProps);
        UserRepoInterface userRepo = new UserRepoORM(sessionFactory);
        Validator<Match> matchValidator=new MatchValidator();
        MatchRepoInterface matchRepo=new MatchRepo(serverProps,matchValidator);
        TicketRepoInterface ticketRepo=new TicketRepo(serverProps,matchRepo);
        ServiceInterface service=new Service(userRepo,matchRepo,ticketRepo);

        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);

        AbstractServer server = new RpcConcurrentServer(chatServerPort, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
            close();
        }finally {
            try {
                server.stop();
                close();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
                close();
            }
        }
    }
}
