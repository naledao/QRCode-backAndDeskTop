package xqq.kangnasi.xyz.webviewdemo.controller;


import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xqq.kangnasi.xyz.webviewdemo.domain.vo.QRCodeVo;
import xqq.kangnasi.xyz.webviewdemo.service.XlsxService;

import java.io.IOException;
import java.util.List;


@RequestMapping("/xlsx")
@RestController
@CrossOrigin("*")
public class XlsxController {
    @Autowired
    private XlsxService xlsxService;

    @PostMapping("up")
    private List<QRCodeVo> upAndGetQRCode(@RequestParam("xlsx") MultipartFile xlsx) throws IOException, WriterException {
        return xlsxService.getARCode(xlsx);
    }

    @GetMapping("/base64")
    private String getBase64(@RequestParam("url") String url,@RequestParam("codeId") String codeId) throws IOException, WriterException {
        return xlsxService.getBase64(url,codeId);
    }
}
