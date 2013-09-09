package edu.cmu.carannotationv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.content.Context;

//明文： plaintext
public class EncryptedData {

	
	private static final String KEY_ALGORITHM = "AES";  
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"; 
	private final static String keyFileName="key.dat";
	private final static String plaintextFileName="showstring.dat";
	private File keyFile;
	private File plaintextFile;
	

	
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
	
	//constructor1: with default file name for encryption data
	public EncryptedData(Context context){
		CreateEncryptedData(context,R.raw.key,R.raw.plaintext);
		
	}
	//constructor2: with raw data identified
	public EncryptedData(Context context, int key, int plaintext) {
		CreateEncryptedData( context,  key,  plaintext);
	}

	private void CreateEncryptedData(Context context, int key, int plaintext) {
		try {
 		   keyFileInputStream=getInputStreamFromRaw(context, key, keyFile	, keyFileName);
 		   plaintextFileInputStream=getInputStreamFromRaw(context, plaintext, plaintextFile, plaintextFileName);

 			setCipherTextClassName(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream,      0, 16, 0, 32));
 			setCipherTextUserName(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream,       0, 16, 0, 48));
 			setCipherTextUserPassword(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream,   0, 16, 0, 48));
 			setCipherTextApplicationId(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream,  0, 16, 0, 48));
 			setCipherTextClientKey(decryptFromPlainKey(keyFileInputStream, plaintextFileInputStream,      0, 16, 0, 48));
 			
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
