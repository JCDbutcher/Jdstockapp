package controllers;

import entity.Usuario;
import funcionalidadDAO.UsuarioDAOImpl;
import interfaceDAO.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.PasswordUtils;
import utils.SessionManager;

import java.io.IOException;

public class ControllerLogCreateUser  {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnSignin;
    @FXML
    private Button btnSignup;
    @FXML
    private Button btnforgotpass;
    @FXML
    private Label lblErrors;
    @FXML
    private Button btnlenguage;
    @FXML
    private Pagination imagePagination;

    private Usuario usuario;




    @FXML
    private void handleSignInAction(ActionEvent event) {
        lblErrors.setText("");

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblErrors.setText("Por favor ingresa tu nombre de usuario y contraseña.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAOImpl();
            Usuario usuario = dao.encontrarUsuarioPorNombreUsuarioLogin(username);

            if (usuario != null && PasswordUtils.verifyPassword(password, usuario.getHashContrasena())) {
                // Almacenar el usuario en la sesión
                SessionManager.getInstance().setCurrentUser(usuario);
                lblErrors.setText("Inicio de sesión exitoso.");
                openDashboard();
            } else {
                lblErrors.setText("Nombre de usuario o contraseña inválido.");
            }
        } catch (Exception e) {
            lblErrors.setText("Error al conectar con la base de datos.");
            e.printStackTrace();  // Registra la excepción para fines de depuración.
        }
    }





    @FXML
    public void handleLanguageChangeAction(ActionEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/uifxml/LanguageSelectionDialog.fxml"));
            Parent root = fxmlLoader.load();


            Stage stage = new Stage();
            stage.setTitle("Select Language");
            stage.setScene(new Scene(root));


            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());


            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void handleSignUpAction(ActionEvent event) {
        openRegistrationForm();
    }



    @FXML
    private void handleForgotPasswordAction(ActionEvent event) {
        System.out.println("Forgot Password button clicked");
    }



    @FXML
    private void initialize() {
        imagePagination.setPageCount(3); // Número de páginas
        imagePagination.setPageFactory(this::createPage); // Método que genera el contenido de cada página
    }

    private Node createPage(Integer pageIndex) {
        ImageView imageView = new ImageView();
        try {
            String imagePath = switch (pageIndex) {
                case 0 -> "/images/inventori_pagination1.jpg";
                case 1 -> "/images/inventori_pagination2.jpg";
                case 2 -> "/images/inventori_pagination3.png";
                default -> null;
            };
            if (imagePath != null) {
                Image image = new Image(getClass().getResource(imagePath).toExternalForm());
                imageView.setImage(image);
            }
        } catch (NullPointerException e) {
            System.err.println("Error loading image for page: " + pageIndex);
            // Handle error or set a default image
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);
        StackPane page = new StackPane(imageView);
        page.setPadding(new Insets(5));
        return page;
    }
    private void openRegistrationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/registrationFormEmpresa.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);
            // Permitir maximizar la ventana
            stage.setMaximized(true);
            ControllerDashboard dashboardController = loader.getController();
            dashboardController.loadInitialView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
