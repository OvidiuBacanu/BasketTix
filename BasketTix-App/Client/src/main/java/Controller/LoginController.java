package Controller;

import Domain.User;
import Services.ServiceInterface;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class LoginController {
    private static Scene scene;
    ServiceInterface service;
    Stage stage;
    MainController mainController;

    @FXML
    TextField usernameText;

    @FXML
    TextField passwordText;

    @FXML
    Button loginButton;

    public LoginController(){}

    public LoginController(ServiceInterface service){
        this.service = service;
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        LoginController.scene = scene;
    }

    public ServiceInterface getService() {
        return service;
    }

    public void setService(ServiceInterface service) {
        this.service = service;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void init() throws IOException {
        stage=new Stage();
        stage.setTitle("Login");
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("/Views/loginView.fxml"));
        AnchorPane layout = Loader.load();
        LoginController l=Loader.getController();
        l.setService(this.service);
        l.setStage(this.stage);
        scene=new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void handleLogin() throws Exception {
        String username=usernameText.getText();
        String password=passwordText.getText();
        User user=new User(username,password);

        AnchorPane root = loadMainController(user);
        user=service.findUserfromLogin(username,password,mainController);

        if(user!=null) {
            mainController.setUser(user);
            stage.setScene(new Scene(root));
            stage.setTitle(user.getUsername());
            stage.show();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Failed login");
            alert.setContentText("Wrong username or password.\n Try again.");
            alert.show();
        }
    }

    private AnchorPane loadMainController(User user) throws Exception {
        FXMLLoader loader=new FXMLLoader();
        URL location=getClass().getResource("/Views/mainView.fxml");
        loader.setLocation(location);
        AnchorPane root = loader.load();

        mainController=loader.getController();
        mainController.setService(service);
        mainController.setUser(user);
        mainController.init();
        return root;
    }

}
