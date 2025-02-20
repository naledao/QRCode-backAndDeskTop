package xqq.kangnasi.xyz.webviewdemo.service;

import com.google.zxing.WriterException;
import org.springframework.web.multipart.MultipartFile;
import xqq.kangnasi.xyz.webviewdemo.domain.vo.QRCodeVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface XlsxService {
    List<QRCodeVo> getARCode(MultipartFile xlsx) throws IOException, WriterException;

    String getBase64(String url,String codeId) throws IOException, WriterException;
}
