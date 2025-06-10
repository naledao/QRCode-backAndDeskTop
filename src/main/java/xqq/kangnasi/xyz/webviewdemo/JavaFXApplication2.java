package xqq.kangnasi.xyz.webviewdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
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

    /** 设计稿逻辑尺寸（不变） */
    private static final double DESIGN_W = 1400;
    private static final double DESIGN_H = 830;

    private Stage splashStage;

    /* --------- ① 计算缩放因子，只“缩小”不强制放大 --------- */
    private double calcScale() {
        double dpiScale = 96.0 / Screen.getPrimary().getDpi();         // 100 %→1.0，150 %→0.667
        Rectangle2D vb   = Screen.getPrimary().getVisualBounds();      // 去掉任务栏
        double sizeScale = Math.min(vb.getWidth()  / DESIGN_W,
                vb.getHeight() / DESIGN_H);
        return Math.min(dpiScale, sizeScale);                          // 只压缩
    }

    /* ---------------- 程序入口 ---------------- */
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        createSplashStage();
        splashStage.show();

        new Thread(() -> {
            SpringApplication app = new SpringApplication(WebviewDemoApplication.class);
            app.addListeners((ApplicationListener<ApplicationReadyEvent>) e ->
                    Platform.runLater(() -> {
                        splashStage.close();
                        try {
                            configureMainStage(primaryStage);
                            primaryStage.show();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }));
            app.run(getParameters().getRaw().toArray(new String[0]));
        }).start();
    }

    /* ---------------- 主窗口 ---------------- */
    private void configureMainStage(Stage stage) throws IOException {
        double initScale = calcScale();                        // 例如 0.667（150 % DPI）

        /* WebView + BorderPane */
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load("http://127.0.0.1:16300/");                // 你的前端入口

        BorderPane root = new BorderPane(webView);

        /* Scene 初始逻辑尺寸 = 设计 × initScale */
        Scene scene = new Scene(root, DESIGN_W * initScale, DESIGN_H * initScale);

        /* --------- ② 绑定 zoom = scene.width / DESIGN_W --------- */
        DoubleBinding zoomBinding = scene.widthProperty().divide(DESIGN_W);
        webView.zoomProperty().bind(zoomBinding);

        /* --------- ③ 可拉伸，保持宽高比 --------- */
        stage.setResizable(true);
        stage.widthProperty().addListener((o, ov, nv) ->
                stage.setHeight(nv.doubleValue() * DESIGN_H / DESIGN_W));

        stage.setScene(scene);
        stage.setTitle("二维码生成器");
        stage.centerOnScreen();

        /* 设置图标 */
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

        /* 最小尺寸防止拖得过小 */
        stage.setMinWidth (DESIGN_W * 0.5);   // 700
        stage.setMinHeight(DESIGN_H * 0.5);   // 415
    }

    /* ---------------- 启动画面 ---------------- */
    private void createSplashStage() {
        splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setAlwaysOnTop(true);

        WebView web = new WebView();
        try {
            URL html = getClass().getResource("/static/start.html");
            web.getEngine().load(html != null
                    ? html.toExternalForm()
                    : "<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        } catch (Exception e) {
            web.getEngine().loadContent("<h1 style='color:red;text-align:center'>启动界面加载失败</h1>");
        }

        Scene sc = new Scene(web, 400, 300);
        splashStage.setScene(sc);
        splashStage.centerOnScreen();

        URL icon = getClass().getResource("/icon.jpg");
        if (icon != null) {
            try { splashStage.getIcons().add(new Image(icon.openStream())); }
            catch (IOException ignored) {}
        }
    }
}
