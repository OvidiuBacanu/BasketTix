package Controller;

import Domain.Match;
import Domain.User;
import Domain.Validator.ValidationException;
import Services.Observer;
import Services.ServiceInterface;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

public class MainController implements Observer {
    private static Scene scene;
    ServiceInterface service;
    Stage stage;
    User user;

    ObservableList<Match> matches=FXCollections.observableArrayList();

    @FXML
    private TextField customerNameText;

    @FXML
    private TextField nrSeatsText;

    @FXML
    private Button sellButton;

    @FXML
    private TextField selectedMatchText;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Match> matchesTable;

    @FXML
    private TableColumn<Match, String> nameColumn;

    @FXML
    private TableColumn<Match, String> nameTeam1Column;

    @FXML
    private TableColumn<Match, String> nameTeam2Column;

    @FXML
    private TableColumn<Match, Double> priceColumn;

    @FXML
    private TableColumn<Match, String> seatsColumn;


    public MainController(ServiceInterface service, User user) {
        this.service = service;
        this.user = user;
    }

    public MainController() { }
    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        MainController.scene = scene;
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

    public void initMatches() throws Exception {
        try{
            matches.setAll(service.getAllMatchesList());
        }catch (Exception e){

        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) throws Exception {
        this.user = user;
        try {
            initMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        matchesTable.setItems(matches);
    }

    public void initTable(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTeam1Column.setCellValueFactory(new PropertyValueFactory<>("nameTeam1"));
        nameTeam2Column.setCellValueFactory(new PropertyValueFactory<>("nameTeam2"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        seatsColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<String>(param.getValue().getNrAvailableSeatsAsString()));
//
        matchesTable.setRowFactory(tv -> new TableRow<Match>() {
            @Override
            protected void updateItem(Match item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null)
                    setStyle("");
                else if (item.getNrAvailableSeats() > 0)
                    setStyle("-fx-background-color: #baffba;");
                else
                    setStyle("-fx-background-color: #ffd7d1;");
            }
        });

        matchesTable.setItems(matches);

        matchesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Match selected=matchesTable.getSelectionModel().getSelectedItem();
                if(selected!=null){
                    selectedMatchText.setText(selected.getName());
                }
            }
        });
    }

    public void init() throws Exception {
        initMatches();
        initTable();
       //service.addObserver(this);
        selectedMatchText.setEditable(false);
    }

    @FXML
    public void handleSearch() throws Exception {
        matches.setAll(service.sortMatchesAfterNrAvailableSeats());
        matchesTable.setItems(matches);
    }

    @FXML
    public void handleSellTicket() {
        String customer=customerNameText.getText();
        int nrSeats=Integer.parseInt(nrSeatsText.getText());
        Match match=matchesTable.getSelectionModel().getSelectedItem();
        if(!customerNameText.getText().isEmpty() && !nrSeatsText.getText().isEmpty() && match!=null) {
            try {
                service.sellTicket(customer, nrSeats, match);
            }
            catch (ValidationException ve){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed sell");
                alert.setContentText(ve.getMessage());
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void update(List<Match> matchList)  {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                matches.setAll(matchList);
                matchesTable.setItems(matches);
            }
        });
    }
}
