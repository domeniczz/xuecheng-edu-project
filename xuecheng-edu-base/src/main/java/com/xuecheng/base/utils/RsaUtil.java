package com.xuecheng.base.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * @author Domenic
 * @Classname RsaUtil
 * @Description RSA 加密工具类
 * @Created by Domenic
 */
public class RsaUtil {

	private RsaUtil() {
		// prevents other classes from instantiating it
	}

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	/**
	 * Explain each part:
	 * 1. "RSA": The encryption algorithm used.
	 * 2. "None": Refers to the mode of operation for the cipher.
	 * 3. "OAEPWITHSHA-256ANDMGF1PADDING": Refers to the padding scheme being used by the RSA encryption.
	 */
	private static final String RSA_ALGORITHM = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";
	private static final Base64.Encoder ENCODER = Base64.getEncoder();
	private static final Base64.Decoder DECODER = Base64.getDecoder();

	/**
	 * <p>
	 * 使用私钥执行 RSA 签名，并将结果签名作为 Base64 编码的字符串返回<br/>
	 * 接收参数：待签名的数据、私钥、编码格式
	 * </p>
	 * @param content 待签名数据
	 * @param privateKey 商户私钥
	 * @param inputCharset 编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey, Charset inputCharset) {
		try {
			// 它使用 RSA 算法和私钥初始化签名实例
			KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
			PKCS8EncodedKeySpec pripkc8 = new PKCS8EncodedKeySpec(DECODER.decode(privateKey));
			PrivateKey priKey = factory.generatePrivate(pripkc8);

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(inputCharset));

			// 对数据进行签名
			byte[] signed = signature.sign();

			// 返回 Base64 编码的签名
			return ENCODER.encodeToString(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <p>
	 * 验证 RSA 签名<br/>
	 * 接受参数：待签名的数据、签名值、公钥、编码格式
	 * </p>
	 * @param content 待签名数据
	 * @param sign 签名值
	 * @param publicKey 公钥
	 * @param inputCharset 编码格式
	 * @return 签名是否有效 {@code true} or {@code false}
	 */
	public static boolean verify(String content, String sign, String publicKey, Charset inputCharset) {
		try {
			KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] encodedKey = DECODER.decode(publicKey);
			PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(encodedKey));

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

			// 使用公钥初始化一个签名验证器
			signature.initVerify(pubKey);
			// 将数据更新到验证器中
			signature.update(content.getBytes(inputCharset));

			// 验证签名
			return signature.verify(DECODER.decode(sign));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * <p>
	 * 使用 RSA 加密算法解密密文<br/>
	 * 接收参数：密文、私钥、编码格式
	 * </p>
	 * @param content 密文
	 * @param privateKey 私钥
	 * @param inputCharset 编码格式
	 * @return 解密后的字符串
	 */
	public static String decrypt(String content, String privateKey, Charset inputCharset) throws IOException, GeneralSecurityException {
		Cipher cipher = null;

		// 初始化用于解密的 Cipher 实例
		cipher = Cipher.getInstance(RSA_ALGORITHM);
		PrivateKey prikey = getPrivateKey(privateKey);
		cipher.init(Cipher.DECRYPT_MODE, prikey);

		// 读入密文，解密，返回明文
		try (InputStream ins = new ByteArrayInputStream(DECODER.decode(content));
				ByteArrayOutputStream writer = new ByteArrayOutputStream()) {

			// RSA 解密的字节的大小最多是 128，因此将需要解密的内容，按 128 位拆开解密，逐块解密
			byte[] buffer = new byte[128];
			int bufferLen;

			while ((bufferLen = ins.read(buffer)) != -1) {
				byte[] block;

				if (buffer.length == bufferLen) {
					block = buffer;
				} else {
					block = Arrays.copyOf(buffer, bufferLen);
				}

				byte[] doFinal = cipher.doFinal(block);
				writer.write(doFinal);
			}

			return new String(writer.toByteArray(), inputCharset);
		}
	}

	/**
	 * <p>
	 * 得到私钥<br/>
	 * 接受 Base64 编码的字符串作为输入，将其转换为字节数组，再从字节数组生成私钥
	 * </p>
	 * @param key {@link PrivateKey} 密钥字符串 (经过 Base64 编码)
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

		byte[] keyBytes;

		keyBytes = DECODER.decode(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		PrivateKey privateKey = null;

		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;
	}

	/**
	 * 使用 RSA 算法生成一个 512 位的公私钥对
	 * @return KeyPair {@link KeyPair} 密钥对
	 */
	public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);

		// 可以理解为：加密后的密文长度，实际原文要小些，越大 加密解密越慢
		keyGen.initialize(512);
		return keyGen.generateKeyPair();
	}

	public static void main(String[] args) {

		org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RsaUtil.class);

		String p2pPublickey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKZKjaBEvudPDolCyuVCBLmfVsSFBu3wfdldLxItRcjSYMzHNoIuYcvHhnMmMi1iXRLeYdbwvI3JQoBHDGN5ad0CAwEAAQ==";
		String p2pPrivatekey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEApkqNoES+508OiULK5UIEuZ9WxIUG7fB92V0vEi1FyNJgzMc2gi5hy8eGcyYyLWJdEt5h1vC8jclCgEcMY3lp3QIDAQABAkAUhQia6UDBXEEH8QUGazIYEbBsSZoETHPLGbOQQ6Pj1tb6CVC57kioBjwtNBnY2jBDWi5K815LnOBcJSSjJPwhAiEA2eO6VZMTkdjQAkpB5dhy/0C3i8zs0c0M1rPoTA/RpkUCIQDDYHJPqHLkQyd//7sEeYcm8cMBTvDKBXyiuGk8eLRauQIgQo6IlalGmg+Dgp+SP5Z9kjD/oCmp0XB0UoVEGS/f140CIQCsG9YXHgi31ACD3T9eHcBVKjvidyveix7UKSdrQdl+4QIgNCtRVLV+783e7PX5hRXD+knsWTQxDEMEsHi1KsAWtPk=";

		String depositoryPublickey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJKcP4TjCb9+OKf0uvHkDO6njI8b9KKlu3ZdCkom4SONf8KkZ1jVl6A7XWnJ33gBLnbTGVUm5I+XvFEG5bSWVbkCAwEAAQ==";
		String depositoryPrivatekey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAkpw/hOMJv344p/S68eQM7qeMjxv0oqW7dl0KSibhI41/wqRnWNWXoDtdacnfeAEudtMZVSbkj5e8UQbltJZVuQIDAQABAkBrkkVw5X0DikNbyM9aKG/ss/cIEgT/SgcwI7gnDDvo7wntxxPuVZ7P+gkhFqb1ByCLdH/GlsXEZW88HCA9M2ZhAiEA65BsW0uGPhnVRS7hJhLZpuuugKVNyJBBO6jGATe0g/UCIQCfVEZ0bvYd5pA165XwXs7ZFGU99rG410EEh7JRxzx0NQIgdNL9ShGck/PP1y22r2Et3CCKPHa+qrcQAvxipnvv5HkCIBITUoblC8DqplOnrXP+nYLdIHs+IH1y1ip4Zo+GheI9AiBdsG0ql4Unbt1ctYm6XdmqE5rdFD+iDFQRS1FFmUVNUQ==";

		Charset inputCharset = StandardCharsets.UTF_8;

		String content = "加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890加密原文1234567890";

		log.debug("----------------- P2P 向存管发送数据 ---------------------");

		String signature1 = RsaUtil.sign(content, p2pPrivatekey, inputCharset);

		log.debug("生成签名, 原文为 {}", content);
		log.debug("生成签名, 签名为 {}", signature1);

		if (RsaUtil.verify(content, signature1, p2pPublickey, inputCharset)) {
			log.debug("验证签名成功：{}", signature1);
		} else {
			log.debug("验证签名失败！");
		}

		log.debug("----------------- 存管向 P2P 返回数据 ---------------------");

		String signature2 = RsaUtil.sign(content, depositoryPrivatekey, inputCharset);

		log.debug("生成签名, 原文为 {}", content);
		log.debug("生成签名, 签名为 {}", signature2);

		if (RsaUtil.verify(content, signature2, depositoryPublickey, inputCharset)) {
			log.debug("验证签名成功：{}", signature2);
		} else {
			log.debug("验证签名失败！");
		}

	}

}