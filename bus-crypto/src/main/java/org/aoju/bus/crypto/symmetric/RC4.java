package org.aoju.bus.crypto.symmetric;

import org.aoju.bus.core.lang.exception.CommonException;
import org.aoju.bus.core.utils.StringUtils;

import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * RC4加密解密算法实现<br>
 *
 * @author aoju.org
 * @version 3.0.1
 * @group 839128
 * @since JDK 1.8
 */
public class RC4 {

    private static final int SBOX_LENGTH = 256;
    /**
     * 密钥最小长度
     */
    private static final int KEY_MIN_LENGTH = 5;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Key array
     */
    private byte[] key;
    /**
     * Sbox
     */
    private int[] sbox;

    /**
     * 构造
     *
     * @param key 密钥
     * @throws CommonException
     */
    public RC4(String key) throws CommonException {
        setKey(key);
    }

    /**
     * 加密
     *
     * @param message 消息
     * @param charset 编码
     * @return 密文
     * @throws CommonException key长度小于5或者大于255抛出此异常
     */
    public byte[] encrypt(String message, Charset charset) throws CommonException {
        return crypt(StringUtils.bytes(message, charset));
    }

    /**
     * 加密，使用默认编码：UTF-8
     *
     * @param message 消息
     * @return 密文
     * @throws CommonException key长度小于5或者大于255抛出此异常
     */
    public byte[] encrypt(String message) throws CommonException {
        return encrypt(message, org.aoju.bus.core.consts.Charset.UTF_8);
    }

    /**
     * 解密
     *
     * @param message 消息
     * @param charset 编码
     * @return 明文
     * @throws CommonException key长度小于5或者大于255抛出此异常
     */
    public String decrypt(byte[] message, Charset charset) throws CommonException {
        return StringUtils.str(crypt(message), charset);
    }

    /**
     * 解密，使用默认编码UTF-8
     *
     * @param message 消息
     * @return 明文
     * @throws CommonException key长度小于5或者大于255抛出此异常
     */
    public String decrypt(byte[] message) throws CommonException {
        return decrypt(message, org.aoju.bus.core.consts.Charset.UTF_8);
    }

    /**
     * 加密或解密指定值，调用此方法前需初始化密钥
     *
     * @param msg 要加密或解密的消息
     * @return 加密或解密后的值
     */
    public byte[] crypt(final byte[] msg) {
        final ReadLock readLock = this.lock.readLock();
        readLock.lock();
        byte[] code;
        try {
            final int[] sbox = this.sbox.clone();
            code = new byte[msg.length];
            int i = 0;
            int j = 0;
            for (int n = 0; n < msg.length; n++) {
                i = (i + 1) % SBOX_LENGTH;
                j = (j + sbox[i]) % SBOX_LENGTH;
                swap(i, j, sbox);
                int rand = sbox[(sbox[i] + sbox[j]) % SBOX_LENGTH];
                code[n] = (byte) (rand ^ msg[n]);
            }
        } finally {
            readLock.unlock();
        }
        return code;
    }

    /**
     * 设置密钥
     *
     * @param key 密钥
     * @throws CommonException key长度小于5或者大于255抛出此异常
     */
    public void setKey(String key) throws CommonException {
        final int length = key.length();
        if (length < KEY_MIN_LENGTH || length >= SBOX_LENGTH) {
            throw new CommonException("Key length has to be between {} and {}", KEY_MIN_LENGTH, (SBOX_LENGTH - 1));
        }

        final WriteLock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.key = StringUtils.bytes(key);
            this.sbox = initSBox(this.key);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 初始化Sbox
     *
     * @param key 密钥
     * @return sbox
     */
    private int[] initSBox(byte[] key) {
        int[] sbox = new int[SBOX_LENGTH];
        int j = 0;

        for (int i = 0; i < SBOX_LENGTH; i++) {
            sbox[i] = i;
        }

        for (int i = 0; i < SBOX_LENGTH; i++) {
            j = (j + sbox[i] + (key[i % key.length]) & 0xFF) % SBOX_LENGTH;
            swap(i, j, sbox);
        }
        return sbox;
    }

    /**
     * 交换指定两个位置的值
     *
     * @param i    位置1
     * @param j    位置2
     * @param sbox 数组
     */
    private void swap(int i, int j, int[] sbox) {
        int temp = sbox[i];
        sbox[i] = sbox[j];
        sbox[j] = temp;
    }

}