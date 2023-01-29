package RPC_Protocol;

import Domain.Match;
import Domain.User;
import Domain.Validator.ValidationException;
import Services.Observer;
import Services.ServiceInterface;
import dto.DTOUtils;
import dto.TicketDTO;
import dto.UserDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ServicesRpcProxy implements ServiceInterface {
    private String host;
    private int port;

    private Observer client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }


    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request)throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse(){
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response) throws Exception {
        if (response.type()==ResponseType.UPDATE){
            List<Match> rez=(ArrayList<Match>) response.data();
            System.out.println("Updating matches");
            try {
                client.update(rez);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.UPDATE;
    }

    @Override
    public User findUserfromLogin(String name, String password, Observer observer) throws Exception {
        initializeConnection();
        UserDTO udto=new UserDTO(name,password);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(udto).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=observer;
            UserDTO user_dto=(UserDTO)response.data();
            User user= DTOUtils.getFromDTO(user_dto);
            return user;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            return null;
        }
        return null;
    }

    @Override
    public List<Match> getAllMatchesList() throws Exception {
        Request req=new Request.Builder().type(RequestType.GET_MATCHES).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            //Match[] matchesDTO=(Match[])response.data();
            //List<Match> rez=DTOUtils.getFromDTO(matchesDTO);
            List<Match> rez=(List<Match>)response.data();
            return rez;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            return null;
        }
        return null;
    }

    @Override
    public void sellTicket(String customerName, int nrSeats, Match match) throws Exception {
        TicketDTO ticketDTO=new TicketDTO(customerName,nrSeats,match);
        Request req=new Request.Builder().type(RequestType.SELL_TICKET).data(ticketDTO).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ValidationException(err);
        }
    }

    @Override
    public List<Match> sortMatchesAfterNrAvailableSeats() throws Exception {
        Request req=new Request.Builder().type(RequestType.GET_SORTED_MATCHES).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            //Match[] matchesDTO=(Match[])response.data();
            //List<Match> rez=DTOUtils.getFromDTO(matchesDTO);
            List<Match> rez=(List<Match>)response.data();
            return rez;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            return null;
        }
        return null;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (Exception e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
