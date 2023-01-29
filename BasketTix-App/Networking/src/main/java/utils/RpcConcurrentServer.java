package utils;

import RPC_Protocol.ClientRpcWorker;
import Services.ServiceInterface;
import java.net.Socket;


public class RpcConcurrentServer extends AbsConcurrentServer {
    private ServiceInterface service;
    public RpcConcurrentServer(int port, ServiceInterface service) {
        super(port);
        this.service = service;
        System.out.println("RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker=new ClientRpcWorker(service, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
