package xqq.kangnasi.xyz.webviewdemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class JavaFXApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load("http://127.0.0.1:16300");
        BorderPane root = new BorderPane();
        root.setCenter(webView);

        Scene scene = new Scene(root, 1400, 830);
        stage.setTitle("二维码生成器");
        stage.setScene(scene);
        stage.setResizable(false); // 禁用窗口调整大小

        URL resource = getClass().getResource("/icon.jpg");
        Image image;
        if (resource != null) {
            image = new Image(resource.openStream());
        } else {
            image = null;
        }
        stage.getIcons().add(image);

        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("请选择");
            alert.setHeaderText("你确定要退出吗？");

            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/icon.jpg"))
            ));

            ButtonType yesButton = new ButtonType("是的", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("取消", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                System.exit(0);
            } else {
                event.consume();
            }
        });
        stage.show();
    }
}
