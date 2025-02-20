package xqq.kangnasi.xyz.webviewdemo.service.impl;

import com.google.zxing.WriterException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xqq.kangnasi.xyz.webviewdemo.api.XlsxApi;
import xqq.kangnasi.xyz.webviewdemo.domain.vo.QRCodeVo;
import xqq.kangnasi.xyz.webviewdemo.service.XlsxService;
import xqq.kangnasi.xyz.webviewdemo.util.QRCodeUtil;
import xqq.kangnasi.xyz.webviewdemo.util.XlsxUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XlsxServiceImpl implements XlsxService {
    @Override
    public List<QRCodeVo> getARCode(MultipartFile xlsx) throws IOException, WriterException {
        if (xlsx==null || xlsx.isEmpty()) {
            return new ArrayList<>();
        }
        List<QRCodeVo> res=new ArrayList<>();
        InputStream inputStream = xlsx.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Row header = sheet.getRow(0);
        out:for (int i=1;i<=sheet.getLastRowNum();i++){
            Row row = sheet.getRow(i);
            List<String> list=new ArrayList<>();
            String codeId="";
            for(int j=0;j<row.getLastCellNum();j++){
                Cell cell = row.getCell(j);
                if(j==0){
                    codeId=XlsxUtil.getCellValue(cell);
                    if(codeId.isEmpty()){
                        break out;
                    }
                }
                String value=XlsxUtil.getCellValue(header.getCell(j))+"："+XlsxUtil.getCellValue(cell);
                list.add(value);
            }
            QRCodeVo qrCodeVo=new QRCodeVo();
            qrCodeVo.setProductionOrderNumber(codeId);
            String qrCodeUrl = XlsxApi.getQRCodeUrl(list, codeId);
            qrCodeVo.setQRCode(qrCodeUrl);
            res.add(qrCodeVo);
        }
        return res;
    }

    @Override
    public String getBase64(String url, String codeId) throws IOException, WriterException {
        if(url==null || url.isEmpty() || codeId==null || codeId.isEmpty()){
            return "";
        }
        return QRCodeUtil.getQRCodeBase64(url,codeId);
    }

}
