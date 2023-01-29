package dto;


import Domain.Ticket;
import Domain.User;

public class DTOUtils {
    public static User getFromDTO(UserDTO usdto){
        String id=usdto.getId();
        String username=usdto.getUsername();
        String pass=usdto.getPassword();
        User u= new User(username, pass);
        u.setId(Long.parseLong(id));
        return u;

    }
    public static UserDTO getDTO(User user){
        String username=user.getUsername();
        String pass=user.getPassword();
        return new UserDTO(username,pass);
    }
}




