package com.rtb.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RefreshScope
@Slf4j
public class JwtTokenProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private String kid;

    public JwtTokenProvider(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
        try{
            Resource privateKeyResource = resourceLoader.getResource(jwtProperties.getPrivateKeyPath());
            Resource publicKeyResource = resourceLoader.getResource(jwtProperties.getPublicKeyPath());

            if(!privateKeyResource.exists()){
               throw new IllegalStateException("Private key not found at path: "+ privateKeyResource);
            }
            if(!publicKeyResource.exists()){
                throw new IllegalStateException("Public key not found at path: "+ publicKeyResource);
            }
            this.privateKey = readPrivateKey(privateKeyResource.getInputStream());
            this.publicKey = readPublicKey(publicKeyResource.getInputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        this.accessTokenExpiration = Math.max(0, jwtProperties.getAccessTokenExpiration());
        this.refreshTokenExpiration = Math.max(0, jwtProperties.getRefreshTokenExpiration());
    }

    private static PrivateKey readPrivateKey(InputStream inputStream) throws Exception {
        String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
        pem = pem.replace("\r", "")
                .replace("\n","");

        if(pem.contains("-----BEGIN PRIVATE KEY-----")){
            String base64 = pem.replace("-----BEGIN PRIVATE KEY-----","")
                    .replace("-----END PRIVATE KEY-----","")
                    .replace("\\s+", "");

            byte[] der = Base64.getDecoder().decode(base64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
        throw new IllegalArgumentException("Unsupported key format. Expected PKCS#8");
    }

    private static PublicKey readPublicKey(InputStream inputStream) throws Exception {
        String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
        pem = pem.replace("\r", "")
                .replace("\n","");

        if(pem.contains("-----BEGIN PUBLIC KEY-----")){
            String base64 = pem.replace("-----BEGIN PUBLIC KEY-----","")
                    .replace("-----END PUBLIC KEY-----","")
                    .replace("\\s+", "");

            byte[] der = Base64.getDecoder().decode(base64);
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        }
        throw new IllegalArgumentException("Unsupported key format. Expected PKCS#8");
    }
}
