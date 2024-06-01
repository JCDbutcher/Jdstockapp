package controllers;

import entity.Empresa;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class ControllerEmpresa {

    @FXML private TextField txtNombre;
    @FXML private TextArea  txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Label lblStatus;

    private Empresa empresaTemp;  // Almacena temporalmente los datos de la empresa

    public void setEmpresa(Empresa empresa) {
        if (empresa != null) {
            txtNombre.setText(empresa.getNombre());
            txtDireccion.setText(empresa.getDireccion());
            txtTelefono.setText(empresa.getTelefono());
            txtEmail.setText(empresa.getEmail());
            empresaTemp = empresa;  // Actualiza la referencia a la empresa temporal
        }
    }

    @FXML
    private void handleSiguienteForm(ActionEvent event) {
        try {
            // Almacenar datos de empresa en un objeto temporal si no existe previamente
            if (empresaTemp == null) {
                empresaTemp = new Empresa();
                empresaTemp.setNombre(txtNombre.getText());
                empresaTemp.setDireccion(txtDireccion.getText());
                empresaTemp.setTelefono(txtTelefono.getText());
                empresaTemp.setEmail(txtEmail.getText());
            }

            // Cambiar al formulario de usuario dentro de la misma ventana
            loadUserForm(event);
        } catch (Exception e) {
            lblStatus.setText("Error al preparar los datos de la empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUserForm(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uifxml/registrationFormUsuario.fxml"));
        Parent root = loader.load();
        ControllerUsuario controllerUsuario = loader.getController();
        controllerUsuario.setEmpresa(empresaTemp);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
