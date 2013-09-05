package edu.cmu.carannotationv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.PrivateCredentialPermission;

import edu.cmu.carannotationv2.R.raw;

import android.R.integer;
import android.content.Context;

//明文： plaintext
public class EncryptedData {
//	public final static String CLASS_NAME = "carannotationinfolongenough";
//	public final static String APP_ID = "GQvxCLxantyoyl2Zo30XIpWyAtbVKa2uCbCSHNry";
//	public final static String CLIENT_KEY = "g2PktGEOsVOUxp6PS5McI9FLNQrbAspF1xsX2MEz";
//	public final static String USR_NAME = "AndroidUserForEncryptionUseLongTime";
//	public final static String PW = "notdefinedmaybenotexistlongenough";
//
//	
	
	private static final String KEY_ALGORITHM = "AES";  
    
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"; 
	
	private final static String keyFileName="key.dat";
	private final static String plaintextFileName="showstring.dat";
	private File keyFile;
	private File plaintextFile;
	
//	private String classname_plaintext;
//	private String classname_decrykey;
//	private String applicationid_plaintext;
//	private String applicationid_decrykey;
//	private String clientkey_plaintext;
//	private String clientkey_decrykey;
//	private String username_plaintext;
//	private String username_decrykey;
//	private String userpassword_plainttext;
//	private String userpassword_decrykey;
//	
	
	
	private String cipherTextClassName;
	private String cipherTextApplicationId;
	private String cipherTextClientKey;
	private String cipherTextUserName;
	private String cipherTextUserPassword;
	
	private FileInputStream keyFileInputStream;
	private FileInputStream plaintextFileInputStream;
	
	private FileInputStream getInputStreamFromRaw(Context context,int csvID, File f,String filename ) throws IOException {
		// TODO Auto-generated method stub
		InputStream isInputStream = context.getResources().openRawResource(csvID);
		File encryptionSpace = context.getDir("encryptionSpace", Context.MODE_PRIVATE);
		f = new File(encryptionSpace, filename);
		FileOutputStream osFileOutputStream = new FileOutputStream(
				f);
		
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = isInputStream.read(buffer)) != -1) {
			osFileOutputStream.write(buffer, 0, bytesRead);

		}
		osFileOutputStream.close();
		isInputStream.close();
		FileInputStream input=new FileInputStream(f);
		return input;
	}
	
	private String decryptFromPlainKey(FileInputStream is_key,FileInputStream is_plaintext,int offset_key, int length_key, int offset_plaintext, int length_plaintext  ) throws Exception{
        byte [] key_barray=new byte[length_key];
        byte [] plaintext_barray=new byte[length_plaintext];
       
        is_key.read(key_barray, offset_key, length_key);
        is_plaintext.read(plaintext_barray, offset_plaintext, length_plaintext);

        
        byte [] decryptionData=decrypt(plaintext_barray, key_barray);
        return  new String(decryptionData);
		
	}
	
	//constructor: everytime try to generate this.
	public EncryptedData(Context context){
		try {
	   keyFileInputStream=getInputStreamFromRaw(context, R.raw.key, keyFile	, keyFileName);
	   plaintextFileInputStream=getInputStreamFromRaw(context, R.raw.showstring, plaintextFile, plaintextFileName);

		setCipherTextClassName(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream, 0, 16, 0, 32));
		setCipherTextApplicationId(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream, 0, 16, 0, 48));
		setCipherTextClientKey(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream, 0, 16, 0, 48));
		setCipherTextUserName(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream, 0	, 16, 0, 48));
		setCipherTextUserPassword(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream, 0, 16, 0, 48));
		
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,byte[] key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,Key key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{  
        //还原密钥  
        Key k = toKey(key);  
        return decrypt(data, k, cipherAlgorithm);  
    }  
  
    /** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{  
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);  
        //使用密钥初始化，设置为解密模式  
        cipher.init(Cipher.DECRYPT_MODE, key);  
        //执行操作  
        return cipher.doFinal(data);  
    }  
    private static Key toKey(byte[] key){  
        //生成密钥  
        return new SecretKeySpec(key, KEY_ALGORITHM);  
    }

	
	public String getCipherTextClassName() {
		return cipherTextClassName;
	}

	public void setCipherTextClassName(String cipherTextClassName) {
		this.cipherTextClassName = cipherTextClassName;
	}

	public String getCipherTextApplicationId() {
		return cipherTextApplicationId;
	}

	public void setCipherTextApplicationId(String cipherTextApplicationId) {
		this.cipherTextApplicationId = cipherTextApplicationId;
	}

	public String getCipherTextClientKey() {
		return cipherTextClientKey;
	}

	public void setCipherTextClientKey(String cipherTextClientKey) {
		this.cipherTextClientKey = cipherTextClientKey;
	}

	public String getCipherTextUserName() {
		return cipherTextUserName;
	}

	public void setCipherTextUserName(String cipherTextUserName) {
		this.cipherTextUserName = cipherTextUserName;
	}

	public String getCipherTextUserPassword() {
		return cipherTextUserPassword;
	}

	public void setCipherTextUserPassword(String cipherTextUserPassword) {
		this.cipherTextUserPassword = cipherTextUserPassword;
	}  
}
