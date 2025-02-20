package xqq.kangnasi.xyz.webviewdemo.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtil {
    public static String getQRCodeBase64(String http,String codeId) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(http, BarcodeFormat.QR_CODE, 300, 300, hints);
        MatrixToImageConfig config = new MatrixToImageConfig();
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
        int totalHeight = 300 + 40;
        BufferedImage combinedImage = new BufferedImage(300, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combinedImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 300, totalHeight);
        g.drawImage(qrImage, 0, 0, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fontMetrics = g.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(codeId);
        int textX = (300 - textWidth) / 2;
        int textY = 300 + 10; // 文字位置
        g.drawString(codeId, textX, textY);
        g.dispose();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(combinedImage, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        return "data:image/png;base64," + base64Image; // 返回 Base64 字符串
    }
}
