package xqq.kangnasi.xyz.webviewdemo.api;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class XlsxApi {
    private static final String baseUrl="http://hhsc.kangnasi.xyz:9660";
    public static String getQRCodeUrl(List<String> list,String codeId){
        WebClient webClient = WebClient.create(baseUrl);
        Mono<String> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/QRCode/encCode")
                        .queryParam("codeId",codeId) // 查询参数
                        .build()) // 替换 {codeId} 为实际值
                .header("Content-Type", "application/json") // 设置请求头
                .bodyValue(list) // 设置请求体
                .retrieve()
                .bodyToMono(String.class);
        String res=response.block();
        if(res==null){
            res=baseUrl+"/QRCode/decCode/"+codeId;
        }
        return res;
    }
}
