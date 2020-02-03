/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.crypto.algorithm.symmetric;

import org.aoju.bus.core.lang.Algorithm;
import org.aoju.bus.core.utils.ArrayUtils;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.crypto.Builder;
import org.aoju.bus.crypto.Mode;
import org.aoju.bus.crypto.Padding;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * AES加密算法实现
 * 高级加密标准（英语：Advanced Encryption Standard,缩写：AES）,在密码学中又称Rijndael加密法
 * 对于Java中AES的默认模式是：AES/ECB/PKCS5Padding,如果使用CryptoJS,请调整为：padding: CryptoJS.pad.Pkcs7
 *
 * <p>
 * 相关概念说明：
 * <pre>
 * mode:    加密算法模式,是用来描述加密算法（此处特指分组密码,不包括流密码,）在加密时对明文分组的模式,它代表了不同的分组方式
 * padding: 补码方式是在分组密码中,当明文长度不是分组长度的整数倍时,需要在最后一个分组中填充一些数据使其凑满一个分组的长度
 * iv:      在对明文分组加密时,会将明文分组与前一个密文分组进行XOR运算（即异或运算）,但是加密第一个明文分组时不存在“前一个密文分组”,
 *          因此需要事先准备一个与分组长度相等的比特序列来代替,这个比特序列就是偏移量
 * </pre>
 * <p>
 *
 * @author Kimi Liu
 * @version 5.5.5
 * @since JDK 1.8+
 */
public class AES extends Symmetric {

    /**
     * 构造,默认AES/ECB/PKCS5Padding,使用随机密钥
     */
    public AES() {
        super(Algorithm.AES);
    }

    /**
     * 构造,使用默认的AES/ECB/PKCS5Padding
     *
     * @param key 密钥
     */
    public AES(byte[] key) {
        super(Algorithm.AES, key);
    }

    /**
     * 构造,使用随机密钥
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     */
    public AES(Mode mode, Padding padding) {
        this(mode.name(), padding.name());
    }

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     */
    public AES(Mode mode, Padding padding, byte[] key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     * @param iv      偏移向量,加盐
     */
    public AES(Mode mode, Padding padding, byte[] key, byte[] iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     */
    public AES(Mode mode, Padding padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     * @param iv      偏移向量,加盐
     */
    public AES(Mode mode, Padding padding, SecretKey key, byte[] iv) {
        this(mode, padding, key, ArrayUtils.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    /**
     * 构造
     *
     * @param mode    模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     * @param iv      偏移向量,加盐
     */
    public AES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     */
    public AES(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     */
    public AES(String mode, String padding, byte[] key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     * @param iv      加盐
     */
    public AES(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding,//
                Builder.generateKey(Algorithm.AES, key),//
                ArrayUtils.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     */
    public AES(String mode, String padding, SecretKey key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥,支持三种密钥长度：128、192、256位
     * @param iv      加盐
     */
    public AES(String mode, String padding, SecretKey key, IvParameterSpec iv) {
        super(StringUtils.format("AES/{}/{}", mode, padding), key, iv);
    }

    /**
     * 设置偏移向量
     *
     * @param iv {@link IvParameterSpec}偏移向量
     * @return 自身
     */
    public AES setIv(IvParameterSpec iv) {
        super.setParams(iv);
        return this;
    }

    /**
     * 设置偏移向量
     *
     * @param iv 偏移向量,加盐
     * @return 自身
     */
    public AES setIv(byte[] iv) {
        setIv(new IvParameterSpec(iv));
        return this;
    }

}