package xqq.kangnasi.xyz.webviewdemo;

import javafx.application.Application;
import javafx.application.Platform;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebviewDemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(WebviewDemoApplication.class,args);
        Application.launch(JavaFXApplication2.class,args);
    }
}
