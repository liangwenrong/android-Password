package com.lwr.password.constant;

public class Constants {
    /**
     * 登录用户
     */
    public static final String PREFERENCES_FILE_NAME_USER = "user";
    /**
     * 密码管理
     */
    public static final String PREFERENCES_FILE_NAME_PASSWORD = "password";    /**
     /**
     * 专门存放配置
     */
    public static final String PREFERENCES_FILE_NAME_CONFIG = "config";
    /**
     * 当前用户的密钥的key，用来获取value
     */
    public final static String CONFIG_KEY = "config_key";
    /**
     * AES密钥字符串，可修改
     */
    private static String AES_KEY = "";
    /**
     * RSA公钥字符串（通过keygen生成）
     */
    public static final String RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQd8+wQYyfajzc8RvsvrMXi02uiLpzZ5Y1E3Kc6LH6lOBLYRd0F8K89uDxA0vWj+NbFKW4AUeG2DDXW1GeMKUykYlD2lR+UmNuoaPymUMxNlwx4ItWBq5TqC+Rl0hhZ0yyhcn5LuKTPideGe7JBPOOo4HIIXOjhd91xDQRW9HjCQIDAQAB";
    /**
     * RSA密钥字符串（通过keygen生成）
     */
    public static final String RSA_PRIVATE_KEY =  "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJB3z7BBjJ9qPNzxG+y+sxeLTa6IunNnljUTcpzosfqU4EthF3QXwrz24PEDS9aP41sUpbgBR4bYMNdbUZ4wpTKRiUPaVH5SY26ho/KZQzE2XDHgi1YGrlOoL5GXSGFnTLKFyfku4pM+J14Z7skE846jgcghc6OF33XENBFb0eMJAgMBAAECgYA7QFh3IrtJGqpxt5XLaH9Ndb3biopmEKMji6FjR3DPpEXFxMF4xNIC7IJfdwmgq40E6xf4utWaMYr+mJ6F8c+eqjtGn+IosKO10K/RGZej1E9ZJM37Jlqv+9IwFH/rfb135Xi61fk3MYGofXWbxpzhPxNtU5bWGt3Dprw1nYVo8QJBANXnG5yxPzccqS8LyT+wfcyGvuMgFsddzZFxem6yDCr96SGbS7UrYuc0lt1meG6kLUm9qMETU0bxGbsEc2I2sM0CQQCs5m2CLPXgjmEPJYVs9QsX5U12l/uEjohLKXKhr94IzS0BUMfepvVDjv5e53zORI/qKkGAW1hCK6lgee5JDAstAkEAwEbJhFWz7HeAAUFH+09MFbC+IG4066t/YREvrBeyZmbtzH7LVCSW+BxGzOgSs+oyp4msCzqy7h9GMN10VsGwtQJAAi0wabgZLEd+j0tAn7uB9T4MuJZ3Mxb8Pj36VHC7Uc8LZv3WVkYxxjTxnM47MvmH+kGn62668LJOAonmZwMAzQJAXLjOZVVF8wLTcvFsG1NmzaiQBcTWSXzul+SYJdrMgcrgnKYn6k9rkJWJUXj1ZrMWdhGdumd0RYdUFulCUn8vzg==";

    public static String getAesKey(){
        return "sd130./dsf(&^^|./~sdodf23433&xc.5_5+95"+AES_KEY;
    }
    public static void setAesKey(String key){
        AES_KEY = key;
    }
}
