package xqq.kangnasi.xyz.webviewdemo.controller;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.springframework.web.bind.annotation.*;
import xqq.kangnasi.xyz.webviewdemo.domain.vo.QRCodeVo;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/local")
@CrossOrigin("*")
public class LocalController {

    @PostMapping("/saveZip")
    public Integer uploadZipFile(@RequestBody List<QRCodeVo> base64List) throws IOException {
        Path tempDir = null;
        // 创建临时目录
        tempDir = Files.createTempDirectory("zip-images");
        List<Path> imageFiles = new ArrayList<>();

        // 解码并保存Base64图片到临时目录
        for (QRCodeVo base64Str : base64List) {
            String[] parts = base64Str.getBase64().split(",", 2);
            String base64Data = parts[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            String filename = base64Str.getProductionOrderNumber() + ".png";
            Path imagePath = tempDir.resolve(filename);
            Files.write(imagePath, imageBytes);
            imageFiles.add(imagePath);
        }
        Platform.runLater(()->{
            // 使用FileChooser选择保存位置
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存ZIP文件");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("ZIP文件", "*.zip")
            );
            fileChooser.setInitialFileName(System.currentTimeMillis()+".zip");
            File selectedFile = fileChooser.showSaveDialog(null);

            if (selectedFile != null) {
                // 确保文件名以.zip结尾
                String filePath = selectedFile.getAbsolutePath();
                if (!filePath.endsWith(".zip")) {
                    selectedFile = new File(filePath + ".zip");
                }

                // 将临时文件打包到ZIP
                try (ZipOutputStream zos = new ZipOutputStream(
                        new FileOutputStream(selectedFile))) {

                    for (Path imagePath : imageFiles) {
                        ZipEntry entry = new ZipEntry(
                                imagePath.getFileName().toString()
                        );
                        zos.putNextEntry(entry);
                        Files.copy(imagePath, zos);
                        zos.closeEntry();
                    }
                }catch (Exception ignored){
                }
                openTargetFile(selectedFile);
            }
        });
        return 1;
    }
    private void openTargetFile(File targetFile){
        // 保存成功后打开目录并定位文件
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                // Windows: 使用explorer选中文件
                String cmd = "explorer /select,\"" + targetFile.getAbsolutePath().replace("\"", "\"\"") + "\"";
                Runtime.getRuntime().exec(cmd);
            } else if (os.contains("mac")) {
                // MacOS: 使用open -R定位文件
                Runtime.getRuntime().exec(new String[]{"open", "-R", targetFile.getAbsolutePath()});
            } else {
                // Linux或其他系统: 打开目录
                Desktop.getDesktop().open(targetFile.getParentFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 回退到仅打开目录
            try {
                Desktop.getDesktop().open(targetFile.getParentFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
