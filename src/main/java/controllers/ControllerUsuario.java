package controllers;

import entity.Empresa;
import entity.RolUsuario;
import entity.Usuario;
import funcionalidadDAO.EmpresaDAOImpl;
import funcionalidadDAO.UsuarioDAOImpl;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.PasswordUtils;

import java.io.IOException;

public class ControllerUsuario {

    @FXML private TextField txtUsuarioNombre;
    @FXML private PasswordField txtUsuarioPassword;
    @FXML private TextField txtNombre;
    @FXML private PasswordField txtRepitaContra;
    @FXML private Label lblStatusUsuario;

    private EmpresaDAOImpl empresaDao = new EmpresaDAOImpl();
    private UsuarioDAOImpl usuarioDao = new UsuarioDAOImpl();

    private Empresa empresaRecibida;
    private EmpresaDAOImpl EmpresaManagmentID;
    public void setEmpresa(Empresa empresa) {
        this.empresaRecibida = empresa;
    }

    @FXML
    private void handleGuardarTodo(ActionEvent event) {
        try {
            // Verificar que las contraseñas coincidan
            if (!txtUsuarioPassword.getText().equals(txtRepitaContra.getText())) {
                lblStatusUsuario.setText("Las contraseñas no coinciden.");
                return; // Salir del método si las contraseñas no coinciden
            }
            // Guardar primero la empresa
            empresaDao.guardar(empresaRecibida);
            // Crear usuario con los datos de la UI
            Usuario nuevoUsuario = new Usuario();
            // Set user details from form
            nuevoUsuario.setNombre(txtNombre.getText());
            nuevoUsuario.setNombreUsuario(txtUsuarioNombre.getText());
            nuevoUsuario.setHashContrasena(PasswordUtils.hashPassword(txtUsuarioPassword.getText()));
            nuevoUsuario.setRol(String.valueOf(RolUsuario.ADMINISTRADOR));
            nuevoUsuario.setActivo(true);
            nuevoUsuario.setEmpresa(empresaRecibida);
            // Luego guardar el usuario
          usuarioDao.guardarUsuario(nuevoUsuario, empresaRecibida.getId());
            lblStatusUsuario.setText("Empresa y usuario creados correctamente.");
            // Cerrar el stage después de 3 segundos
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                Stage stage = (Stage) lblStatusUsuario.getScene().getWindow();
                stage.close();
            });
            delay.play();
        } catch (Exception e) {
            lblStatusUsuario.setText("Error al guardar la empresa y usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegresarARegistroEmpresa(ActionEvent event) {
        try {
            // Carga la vista de registro de empresa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/registrationFormEmpresa.fxml")); // Asegúrate de que la ruta es correcta
            Parent root = loader.load();
            ControllerEmpresa controllerEmpresa = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
