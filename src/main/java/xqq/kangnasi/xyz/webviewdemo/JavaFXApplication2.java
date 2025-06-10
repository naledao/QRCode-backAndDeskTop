package xqq.kangnasi.xyz.webviewdemo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class JavaFXApplication2 extends Application {

    /* ---------- 设计稿尺寸 ---------- */
    private static final double DESIGN_W = 1400;
    private static final double DESIGN_H = 830;

    private Stage splashStage;

    /* ---------- 计算缩放因子 ---------- */
    private double calcScale() {
        Rectangle2D vb = Screen.getPrimary().getVisualBounds(); // 去掉任务栏
        double sx = vb.getWidth()  / DESIGN_W;
        double sy = vb.getHeight() / DESIGN_H;
        return Math.min(sx, sy);                                // 等比缩放
    }

    @Override
    public void start(Stage primaryStage) {
        /* 启动画面 */
        createSplashStage();
        splashStage.show();

        /* 后台启动 Spring Boot */
        new Thread(() -> {
            try {
                SpringApplication app = new SpringApplication(WebviewDemoApplication.class);
                app.addListeners((ApplicationListener<ApplicationReadyEvent>) event ->
                        Platform.runLater(() -> {
                            splashStage.close();
                            try {
                                configureMainStage(primaryStage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            primaryStage.show();
                        }));
                app.run(getParameters().getRaw().toArray(new String[0]));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    splashStage.close();
                    showErrorAlert("Spring Boot 启动失败: " + e.getMessage());
                });
            }
        }).start();
    }

    /* ---------- Splash ---------- */
    private void createSplashStage() {
        double scale = calcScale();

        splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setAlwaysOnTop(true);

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        try {
            URL htmlUrl = getClass().getResource("/static/start.html");
            engine.load(htmlUrl != null ? htmlUrl.toExternalForm()
                    : "<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        } catch (Exception ex) {
            ex.printStackTrace();
            engine.loadContent("<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        }

        webView.setPrefWidth(400 * scale);
        webView.setPrefHeight(300 * scale);

        Scene scene = new Scene(webView);
        scene.getRoot().setScaleX(scale);
        scene.getRoot().setScaleY(scale);

        splashStage.setScene(scene);
        splashStage.centerOnScreen();

        /* 图标 */
        try {
            URL iconUrl = getClass().getResource("/icon.jpg");
            if (iconUrl != null) splashStage.getIcons().add(new Image(iconUrl.openStream()));
        } catch (IOException ignored) {}
    }

    /* ---------- 主窗口 ---------- */
    private void configureMainStage(Stage stage) throws IOException {
        double scale = calcScale();

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load("http://127.0.0.1:16300/");                 // Spring Boot 地址

        BorderPane root = new BorderPane(webView);
        root.setScaleX(scale);
        root.setScaleY(scale);

        Scene scene = new Scene(root, DESIGN_W * scale, DESIGN_H * scale);

        stage.setTitle("二维码生成器");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.centerOnScreen();                                 // 保持居中
        stage.setMinWidth(DESIGN_W * 0.6);
        stage.setMinHeight(DESIGN_H * 0.6);

        /* 图标 */
        URL iconUrl = getClass().getResource("/icon.jpg");
        if (iconUrl != null) stage.getIcons().add(new Image(iconUrl.openStream()));

        /* 关闭确认 */
        stage.setOnCloseRequest(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("请选择");
            alert.setHeaderText("你确定要退出吗？");

            ButtonType yes = new ButtonType("是的", ButtonBar.ButtonData.YES);
            ButtonType no  = new ButtonType("取消", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yes, no);

            /* 对话框图标 */
            Stage dlg = (Stage) alert.getDialogPane().getScene().getWindow();
            if (iconUrl != null) {
                try {
                    dlg.getIcons().add(new Image(iconUrl.openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get() == yes) {
                System.exit(0);               // ★ 点击“是的”后直接退出 JVM
            } else {
                evt.consume();                // 取消关闭
            }
        });

        /* 宽高保持比例（可选） */
        stage.widthProperty().addListener((o, ov, nv) -> {
            double newH = nv.doubleValue() * DESIGN_H / DESIGN_W;
            stage.setHeight(newH);
        });
    }

    /* ---------- 错误提示 ---------- */
    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("启动失败");
        alert.setContentText(msg);
        try {
            URL iconUrl = getClass().getResource("/icon.jpg");
            if (iconUrl != null) {
                Stage dlg = (Stage) alert.getDialogPane().getScene().getWindow();
                dlg.getIcons().add(new Image(iconUrl.openStream()));
            }
        } catch (IOException ignored) {}
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
