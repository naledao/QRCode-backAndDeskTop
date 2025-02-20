package xqq.kangnasi.xyz.webviewdemo.domain.vo;


import lombok.Data;

@Data
public class QRCodeVo {
    private String productionOrderNumber;
    private String QRCode;
    private String base64;
}
