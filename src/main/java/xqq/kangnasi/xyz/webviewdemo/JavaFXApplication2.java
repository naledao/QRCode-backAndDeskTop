/* src/main/java/xqq/kangnasi/xyz/webviewdemo/JavaFXApplication2.java */
package xqq.kangnasi.xyz.webviewdemo;

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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class JavaFXApplication2 extends Application {

    /* 设计稿逻辑尺寸 */
    private static final double DESIGN_W = 1400;
    private static final double DESIGN_H = 830;

    private Stage splashStage;

    /* ==== 缩放因子：DPI 优先，尺寸兜底 ==== */
    private double calcScale() {
        double dpiScale = 96.0 / Screen.getPrimary().getDpi();     // 100 % → 1.0；150 % → 0.667
        Rectangle2D vb = Screen.getPrimary().getVisualBounds();
        double sizeScale = Math.min(vb.getWidth() / DESIGN_W,
                vb.getHeight() / DESIGN_H);
        return Math.min(dpiScale, sizeScale);
    }

    /* ============================= 程序入口 ============================= */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        createSplashStage();
        splashStage.show();

        new Thread(() -> {
            SpringApplication app = new SpringApplication(WebviewDemoApplication.class);
            app.addListeners((ApplicationListener<ApplicationReadyEvent>) evt ->
                    Platform.runLater(() -> {
                        splashStage.close();
                        try {
                            configureMainStage(primaryStage);
                            primaryStage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            app.run(getParameters().getRaw().toArray(new String[0]));
        }).start();
    }

    /* ============================= 主窗口 ============================= */
    private void configureMainStage(Stage stage) throws IOException {
        double scale = calcScale();                               // 例如 0.667

        /* --- WebView --- */
        WebView webView = new WebView();
        webView.setZoom(scale);                                   // 只缩放网页
        WebEngine engine = webView.getEngine();
        engine.load("http://127.0.0.1:16300/");

        BorderPane root = new BorderPane(webView);

        /* Stage 使用 “设计稿 × scale” 的逻辑尺寸 */
        Scene scene = new Scene(root, DESIGN_W * scale, DESIGN_H * scale);
        stage.setScene(scene);
        stage.setTitle("二维码生成器");
        stage.setResizable(false);
        stage.centerOnScreen();

        URL icon = getClass().getResource("/icon.jpg");
        if (icon != null) stage.getIcons().add(new Image(icon.openStream()));

        /* 关闭确认 */
        stage.setOnCloseRequest(evt -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("请选择");
            alert.setHeaderText("你确定要退出吗？");
            ButtonType yes = new ButtonType("是的", ButtonBar.ButtonData.YES);
            ButtonType no  = new ButtonType("取消", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yes, no);

            if (icon != null) {
                try {
                    ((Stage) alert.getDialogPane().getScene().getWindow())
                            .getIcons().add(new Image(icon.openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Optional<ButtonType> rs = alert.showAndWait();
            if (!(rs.isPresent() && rs.get() == yes)) evt.consume();
            else System.exit(0);
        });
    }

    /* ============================= Splash ============================= */
    private void createSplashStage() {
        splashStage = new Stage(StageStyle.UNDECORATED);
        double scale = calcScale();

        WebView web = new WebView();         // 同理仅缩放 WebView
        try {
            URL html = getClass().getResource("/static/start.html");
            web.getEngine().load(html != null
                    ? html.toExternalForm()
                    : "<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        } catch (Exception e) {
            web.getEngine().loadContent("<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        }

        Scene sc = new Scene(web, 400 , 300);
        splashStage.setScene(sc);
        splashStage.setAlwaysOnTop(true);
        splashStage.centerOnScreen();

        URL icon = getClass().getResource("/icon.jpg");
        if (icon != null) {
            try { splashStage.getIcons().add(new Image(icon.openStream())); }
            catch (IOException ignored) {}
        }
    }
}
