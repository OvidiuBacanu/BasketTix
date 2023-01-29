package start;

import Domain.Match;
import Rest_Services.MatchClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicReference;

public class StartRestClient {
    private final static MatchClient matchClient=new MatchClient();

    public static void main(String[] args) {
        RestTemplate restTemplate=new RestTemplate();
        Match testMatch=new Match("acesta","este","din test",38.99,45);
        Match testUpdateMatch=new Match("acesta2","este2","din update",38.99,45);
        testUpdateMatch.setId(11L);

        try{
            //Afisare
            show(()->{
                try {
                    Match[] rez = matchClient.findAll();
                    for(Match match:rez){
                        System.out.println(match);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

//            //Adaugare matchTest
//            show(()-> {
//                try {
//                    System.out.println("\nSAVE");
//                    System.out.println(matchClient.save(testMatch)+"\n");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            //Update match cu id 11
//            show(()-> {
//                try {
//                    System.out.println("\nUPDATE");
//                    matchClient.update(testUpdateMatch);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            //Find match cu id 11
//            show(()-> {
//                try {
//                    System.out.println("\nFIND ONE");
//                    System.out.println(matchClient.findOne(11L)+"\n");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            //Delete match cu id 23
//            show(()-> {
//                try {
//                    System.out.println("\nDELETE");
//                    matchClient.delete(23L);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });

        }catch(RestClientException ex){
            System.out.println("Exception ... "+ex.getMessage());
        }
    }


    private static void show(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            System.out.println("Exception"+ e);
        }
    }
}

