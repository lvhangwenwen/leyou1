package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    private  PublicKey publicKey;
    private PrivateKey privateKey;
    private  String cookieName;
    //对象一旦实例化，就要去读取公钥和私钥
    @PostConstruct
    public  void init() throws Exception {

            //公钥和私钥如果不存在，先生成
        File pubPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);

        if (!pubPath.exists() || !priPath.exists()){
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }

        //读取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey= RsaUtils.getPrivateKey(priKeyPath);


    }
}
