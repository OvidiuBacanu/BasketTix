package RPC_Protocol;

import Domain.Match;
import Domain.User;
import Services.Observer;
import Services.ServiceInterface;
import dto.DTOUtils;
import dto.TicketDTO;
import dto.UserDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientRpcWorker implements Runnable, Observer {
    private ServiceInterface service;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(ServiceInterface service, Socket connection) {
        this.service = service;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();
    //private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private Response handleRequest(Request request) throws Exception {
        Response response=null;
        if (request.type()== RequestType.LOGIN){
            System.out.println("Login request ..."+request.type());
            UserDTO udto=(UserDTO)request.data();

            User log=service.findUserfromLogin(udto.getUsername(), udto.getPassword(),this);
            if(log!=null) {
                udto.setId(log.getId().toString());
                return new Response.Builder().type(ResponseType.OK).data(udto).build();
            }
            else{
                connected=false;
                String error="Wrong username or password";
                return new Response.Builder().type(ResponseType.ERROR).data(error).build();
            }
        }
        if (request.type()== RequestType.GET_MATCHES){
            System.out.println("Get all matches");
            try {
                ArrayList<Match> rez= (ArrayList<Match>) service.getAllMatchesList();
                return new Response.Builder().type(ResponseType.OK).data(rez).build();

            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.GET_SORTED_MATCHES){
            System.out.println("Get sorted matches");
            try {
                ArrayList<Match> rez= (ArrayList<Match>)service.sortMatchesAfterNrAvailableSeats();
                return new Response.Builder().type(ResponseType.OK).data(rez).build();

            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type()== RequestType.SELL_TICKET){
            System.out.println("Sell ticket ...");
            TicketDTO ticketDTO = (TicketDTO) request.data();
            try {
                service.sellTicket(ticketDTO.getCustomerName(),ticketDTO.getNrSeats(),ticketDTO.getMatch());
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (Exception e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output){
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void update(List<Match> matches) throws Exception {
        ArrayList<Match> matchesRez= (ArrayList<Match>) matches;
        Response resp=new Response.Builder().type(ResponseType.UPDATE).data(matchesRez).build();
        System.out.println("UPDATE");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new Exception("Sending error: "+e);
        }
    }
}
