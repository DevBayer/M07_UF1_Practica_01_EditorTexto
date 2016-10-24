package sample;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public File fOpen;
    private Double initfontSize = 10d;
    private String initfontFamily = "System";

    @FXML
    TextArea txt;

    @FXML
    ToolBar toolbar;

    @FXML
    Menu options;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        for (MenuItem submenu:options.getItems()) {
            Menu item = (Menu) submenu;
            for (MenuItem asd:item.getItems()) {
                if(submenu.getText().equals("Font") && asd.getText().equals(initfontFamily)){
                    RadioMenuItem radio = (RadioMenuItem) asd;
                    radio.setSelected(true);
                }
                if(submenu.getText().equals("Tamany") && Double.parseDouble(asd.getText()) == initfontSize){
                    RadioMenuItem radio = (RadioMenuItem) asd;
                    radio.setSelected(true);
                }
            }
        }

        try {
            Image iconCut = new Image(getClass().getResourceAsStream("/icons/cut.png"));
            Image iconCopy = new Image(getClass().getResourceAsStream("/icons/copy.png"));
            Image iconPaste = new Image(getClass().getResourceAsStream("/icons/paste.png"));

            ImageView img;

            img = new ImageView(iconCut);
            img.setFitWidth(16);
            img.setFitHeight(16);
            ((Button) toolbar.getItems().get(0)).setGraphic(img);

            img = new ImageView(iconCopy);
            img.setFitWidth(16);
            img.setFitHeight(16);
            ((Button) toolbar.getItems().get(1)).setGraphic(img);

            img = new ImageView(iconPaste);
            img.setFitWidth(16);
            img.setFitHeight(16);
            ((Button) toolbar.getItems().get(2)).setGraphic(img);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void openAbout() throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("../about/about.fxml"));
        stage.setTitle("Notepad Java++ | Sobre l'Editor");
        stage.setScene(new Scene(root, 350, 280));
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);

        stage.show();
    }

    public void onExit() {
        Platform.exit();
    }

    public void onEdit(ActionEvent actionEvent) {
        String text = "";
        if(actionEvent.getSource().getClass().equals(Button.class)){
            Button btn = (Button) actionEvent.getSource();
            text = btn.getText();
        }else{
            MenuItem menu = (MenuItem) actionEvent.getSource();
            text = menu.getText();
        }

        switch(text){
            case "Copiar":
                txt.copy();
                break;

            case "Tallar":
                txt.cut();
                break;

            case "Enganxar":
                txt.paste();
                break;

            case "Desfer":
                txt.undo();
                break;
        }
    }

    public void changeFontFamily(ActionEvent actionEvent) {
        RadioMenuItem item = (RadioMenuItem) actionEvent.getSource();
        initfontFamily = item.getText();
        Font font = new Font( initfontFamily, initfontSize );
        txt.setFont(font);
    }

    public void changeFontSize(ActionEvent actionEvent) {
        RadioMenuItem item = (RadioMenuItem) actionEvent.getSource();
        initfontSize = Double.parseDouble(item.getText());
        Font font = new Font( initfontFamily, initfontSize );
        txt.setFont(font);
    }

    public void check_CanUseEditar(Event event) {
        if(event.getTarget().getClass().equals(Menu.class)){
            _CanUseEditar( ((Menu) event.getTarget()).getItems() );
        }else{
            _CanUseEditar( ((ContextMenu) event.getTarget()).getItems() );
        }
    }

    private void _CanUseEditar(ObservableList<MenuItem> items){
        for (MenuItem submenu: items) {
            if(txt.getSelectedText().length() > 0 && (submenu.getText().equals("Copiar") || submenu.getText().equals("Tallar"))){
                submenu.setDisable(false);
            }else if(txt.getSelectedText().length() == 0 && (submenu.getText().equals("Copiar") || submenu.getText().equals("Tallar"))){
                submenu.setDisable(true);
            }
            if(submenu.getText().equals("Desfer") && txt.isUndoable()){
                submenu.setDisable(false);
            }else if(submenu.getText().equals("Desfer") && !txt.isUndoable()){
                submenu.setDisable(true);
            }
        }
    }

    public void check_CanUseSave(Event event) {
        Menu menu = (Menu) event.getTarget();
        for (MenuItem submenu: menu.getItems()) {
            if(submenu.getClass().equals(MenuItem.class)){
                if(submenu.getText().equals("Deixar") && fOpen != null){
                    submenu.setDisable(false);
                }else if(submenu.getText().equals("Deixar") && fOpen == null){
                    System.out.println(fOpen);
                    submenu.setDisable(true);
                }
            }
        }
    }

    public void onOpen() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Stage stage = (Stage) txt.getScene().getWindow();
            try{
                openFile(selectedFile);
                stage.setTitle("Notepad Java++ - "+ selectedFile.getName());
                fOpen = selectedFile;
            }catch(Exception e){
                System.out.println("Err: "+e.getMessage());
            }
        }
    }

    public void onSave() {
        if(fOpen != null && fOpen.canWrite()){
            try {
                BufferedWriter output = new BufferedWriter(new FileWriter(fOpen.getAbsoluteFile()));
                output.write(txt.getText());
                output.close();
            }catch(Exception e){
                System.out.println("Err: "+e.getMessage());
            }
        }
    }

    public void onSaveAs() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            System.out.println(selectedFile);
            fOpen = selectedFile;
            Stage stage = (Stage) txt.getScene().getWindow();
            stage.setTitle("Notepad Java++ - "+ selectedFile.getName());
            try {
                BufferedWriter output = new BufferedWriter(new FileWriter(fOpen.getAbsoluteFile()));
                output.write(txt.getText());
                output.close();
            }catch(IOException e){
                System.out.println("Err: "+e.getMessage());
            }
        }
    }

    private void openFile(File file) throws IOException {
        String texto = "";
        Reader reader = new InputStreamReader(new FileInputStream(file));
        int r;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            texto += ch;
        }
        txt.setText(texto);
    }

}
