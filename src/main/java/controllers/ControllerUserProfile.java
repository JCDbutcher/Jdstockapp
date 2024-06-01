package controllers;

import entity.Usuario;
import funcionalidadDAO.UsuarioDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import utils.SessionManager;

public class ControllerUserProfile {

    @FXML private TextField tfNombre;
    @FXML private TextField tfNombreUsuario;
    @FXML private TextField tfContrasena;

    private UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private Usuario currentUser;

    @FXML
    public void initialize() {
        loadCurrentUserDetails();
    }

    private void loadCurrentUserDetails() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            tfNombre.setText(currentUser.getNombre());
            tfNombreUsuario.setText(currentUser.getNombreUsuario());

        }
    }

    @FXML
    private void handleSave(ActionEvent actionEvent) {
        try {
            currentUser.setNombre(tfNombre.getText());
            currentUser.setNombreUsuario(tfNombreUsuario.getText());

            currentUser.setHashContrasena(tfContrasena.getText());

            usuarioDAO.actualizarUsuario(currentUser, currentUser.getEmpresa().getId());
            showAlert("Success", "Profile updated successfully.");
        } catch (Exception e) {
            showAlert("Error", "Failed to update profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent actionEvent) {
        try {
            usuarioDAO.eliminarUsuario(currentUser.getId(), currentUser.getEmpresa().getId());
            SessionManager.getInstance().clearSession();
            closeWindow();
            showAlert("Success", "User deleted successfully.");
        } catch (Exception e) {
            showAlert("Error", "Failed to delete user: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        closeWindow();
    }

    private void closeWindow() {
        tfNombre.getScene().getWindow().hide();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
