package com.leyou.auth.test;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    private static final String pubKeyPath = "G:\\Program Files (x86)\\rsa.pub";

    private static final String priKeyPath = "G:\\Program Files (x86)\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @org.junit.Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @org.junit.Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @org.junit.Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU2MTE5MDExNH0.IMRqVWYxu8ylhj-TJTH3Js8TFbrQbsiJvJo9h1OvOUKBKUAd2nx1iUUaA-XUlQ36nj_2cj-yfMTkvsLdnsMW3iwt0enXJ_djAM-G_UgFry2VVO_LY2vJi8CmhMJJBUuDtXdBbWjNTV7W4GLUeKYownsCr-dBTcO8o81uuA9gAFg";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }

}
