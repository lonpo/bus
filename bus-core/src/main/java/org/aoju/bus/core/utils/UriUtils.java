/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.core.utils;

import org.aoju.bus.core.convert.Convert;
import org.aoju.bus.core.lang.*;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.map.TableMap;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarFile;

/**
 * URL相关工具
 *
 * @author Kimi Liu
 * @version 5.9.1
 * @since JDK 1.8+
 */
public class UriUtils {

    /**
     * 协议，例如http
     */
    private String scheme;
    /**
     * 主机，例如127.0.0.1
     */
    private String host;
    /**
     * 端口，默认-1
     */
    private int port = -1;
    /**
     * 路径，例如/aa/bb/cc
     */
    private Path path;
    /**
     * 查询语句，例如a=1&amp;b=2
     */
    private Query query;
    /**
     * 标识符，例如#后边的部分
     */
    private String fragment;

    /**
     * 编码，用于URLEncode和URLDecode
     */
    private Charset charset;

    /**
     * 构造
     */
    public UriUtils() {
        this.charset = org.aoju.bus.core.lang.Charset.UTF_8;
    }

    /**
     * 构造
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charset  编码，用于URLEncode和URLDecode
     */
    public UriUtils(String scheme, String host, int port, Path path, Query query, String fragment, Charset charset) {
        this.charset = charset;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.setFragment(fragment);
    }

    /**
     * 创建空的UriUtils
     *
     * @return UriUtils
     */
    public static UriUtils create() {
        return new UriUtils();
    }

    /**
     * 使用URL字符串构建UriUtils
     *
     * @param url     URL字符串
     * @param charset 编码，用于URLEncode和URLDecode
     * @return UriUtils
     */
    public static UriUtils of(String url, Charset charset) {
        Assert.notBlank(url, "Url must be not blank!");
        return of(url(url.trim()), charset);
    }

    /**
     * 使用URI构建UriUtils
     *
     * @param uri     URI
     * @param charset 编码，用于URLEncode和URLDecode
     * @return UriUtils
     */
    public static UriUtils of(URI uri, Charset charset) {
        return of(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getRawQuery(), uri.getFragment(), charset);
    }

    /**
     * 使用URL构建UriUtils
     *
     * @param url     URL
     * @param charset 编码，用于URLEncode和URLDecode
     * @return UriUtils
     */
    public static UriUtils of(URL url, Charset charset) {
        return of(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef(), charset);
    }

    /**
     * 构建UriUtils
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charset  编码，用于URLEncode和URLDecode
     * @return UriUtils
     */
    public static UriUtils of(String scheme, String host, int port, String path, String query, String fragment, Charset charset) {
        return of(scheme, host, port, Path.of(path, charset), Query.of(query, charset), fragment, charset);
    }

    /**
     * 构建UriUtils
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charset  编码，用于URLEncode和URLDecode
     * @return UriUtils
     */
    public static UriUtils of(String scheme, String host, int port, Path path, Query query, String fragment, Charset charset) {
        return new UriUtils(scheme, host, port, path, query, fragment, charset);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url URL
     * @return URL对象
     */
    public static URL url(String url) {
        return url(url, null);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url     URL
     * @param handler {@link URLStreamHandler}
     * @return URL对象
     */
    public static URL url(String url, URLStreamHandler handler) {
        Assert.notNull(url, "URL must not be null");

        // 兼容Spring的ClassPath路径
        if (url.startsWith(Normal.CLASSPATH)) {
            url = url.substring(Normal.CLASSPATH.length());
            return ClassUtils.getClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new InstrumentException(e);
            }
        }
    }

    /**
     * 将URL字符串转换为URL对象,并做必要验证
     *
     * @param urlStr URL字符串
     * @return URL
     */
    public static URL toUrlForHttp(String urlStr) {
        return toUrlForHttp(urlStr, null);
    }

    /**
     * 将URL字符串转换为URL对象,并做必要验证
     *
     * @param urlStr  URL字符串
     * @param handler {@link URLStreamHandler}
     * @return URL
     */
    public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
        Assert.notBlank(urlStr, "Url is blank !");
        // 去掉url中的空白符,防止空白符导致的异常
        urlStr = StringUtils.cleanBlank(urlStr);
        return url(urlStr, handler);
    }

    /**
     * 获得URL
     *
     * @param pathBaseClassLoader 相对路径(相对于classes)
     * @return URL
     * @see ResourceUtils#getResource(String)
     */
    public static URL getURL(String pathBaseClassLoader) {
        return ResourceUtils.getResource(pathBaseClassLoader);
    }

    /**
     * 获得URL,常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @throws InstrumentException MalformedURLException
     */
    public static URL getURL(File file) {
        Assert.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new InstrumentException("Error occured when get URL!");
        }
    }

    /**
     * 获得URL,常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     * @throws InstrumentException MalformedURLException
     */
    public static URL[] getURL(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new InstrumentException("Error occured when get URL!");
        }

        return urls;
    }

    /**
     * 获得URL
     *
     * @param path  相对给定 class所在的路径
     * @param clazz 指定class
     * @return URL
     * @see ResourceUtils#getResource(String, Class)
     */
    public static URL getURL(String path, Class<?> clazz) {
        return ResourceUtils.getResource(path, clazz);
    }

    /**
     * 格式化URL链接
     *
     * @param url 需要格式化的URL
     * @return 格式化后的URL, 如果提供了null或者空串, 返回null
     * @see #normalize(String)
     */
    public static String formatUrl(String url) {
        return normalize(url);
    }

    /**
     * 补全相对路径
     *
     * @param baseUrl      基准URL
     * @param relativePath 相对URL
     * @return 相对路径
     * @throws InstrumentException MalformedURLException
     */
    public static String complateUrl(String baseUrl, String relativePath) {
        baseUrl = formatUrl(baseUrl);
        if (StringUtils.isBlank(baseUrl)) {
            return null;
        }

        try {
            final URL absoluteUrl = new URL(baseUrl);
            final URL parseUrl = new URL(absoluteUrl, relativePath);
            return parseUrl.toString();
        } catch (MalformedURLException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 编码URL,默认使用UTF-8编码
     * 将需要转换的内容(ASCII码形式之外的内容),用十六进制表示法转换出来,并在之前加上%开头
     *
     * @param url URL
     * @return 编码后的URL
     * @throws InstrumentException UnsupportedEncodingException
     */
    public static String encode(String url) throws InstrumentException {
        return encode(url, org.aoju.bus.core.lang.Charset.DEFAULT_UTF_8);
    }

    /**
     * 编码URL,默认使用UTF-8编码
     * 将需要转换的内容(ASCII码形式之外的内容),用十六进制表示法转换出来,并在之前加上%开头
     *
     * @param url URL
     * @return 编码后的URL
     */
    public static String encodeAll(String url) {
        return encodeAll(url, org.aoju.bus.core.lang.Charset.UTF_8);
    }

    /**
     * 编码URL
     * 将需要转换的内容(ASCII码形式之外的内容),用十六进制表示法转换出来,并在之前加上%开头
     *
     * @param url     URL
     * @param charset 编码
     * @return 编码后的URL
     */
    public static String encodeAll(String url, Charset charset) {
        try {
            return java.net.URLEncoder.encode(url, charset.toString());
        } catch (UnsupportedEncodingException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 获得path部分
     *
     * @param uriStr URI路径
     * @return path
     * @throws InstrumentException 包装URISyntaxException
     */
    public static String getPath(String uriStr) {
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new InstrumentException(e);
        }
        return uri.getPath();
    }

    /**
     * 从URL对象中获取不被编码的路径Path
     * 对于本地路径,URL对象的getPath方法对于包含中文或空格时会被编码,导致本读路径读取错误
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     *
     * @param url {@link URL}
     * @return 路径
     */
    public static String getDecodedPath(URL url) {
        if (null == url) {
            return null;
        }

        String path = null;
        try {
            // URL对象的getPath方法对于包含中文或空格的问题
            path = toURI(url).getPath();
        } catch (InstrumentException e) {
            // ignore
        }
        return (null != path) ? path : url.getPath();
    }

    /**
     * 转URL为URI
     *
     * @param url URL
     * @return URI
     * @throws InstrumentException 包装URISyntaxException
     */
    public static URI toURI(URL url) throws InstrumentException {
        if (null == url) {
            return null;
        }
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @return URI
     * @throws InstrumentException 包装URISyntaxException
     */
    public static URI toURI(String location) throws InstrumentException {
        try {
            return new URI(location.replace(Symbol.SPACE, "%20"));
        } catch (URISyntaxException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 提供的URL是否为文件
     * 文件协议包括"file", "vfsfile" 或 "vfs".
     *
     * @param url {@link URL}
     * @return 是否为文件
     */
    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (Normal.URL_PROTOCOL_FILE.equals(protocol) || //
                Normal.URL_PROTOCOL_VFSFILE.equals(protocol) || //
                Normal.URL_PROTOCOL_VFS.equals(protocol));
    }

    /**
     * 提供的URL是否为jar包URL 协议包括： "jar", "zip", "vfszip" 或 "wsjar".
     *
     * @param url {@link URL}
     * @return 是否为jar包URL
     */
    public static boolean isJarURL(URL url) {
        final String protocol = url.getProtocol();
        return (Normal.URL_PROTOCOL_JAR.equals(protocol) || //
                Normal.URL_PROTOCOL_ZIP.equals(protocol) || //
                Normal.URL_PROTOCOL_VFSZIP.equals(protocol) || //
                Normal.URL_PROTOCOL_WSJAR.equals(protocol));
    }

    /**
     * 提供的URL是否为Jar文件URL 判断依据为file协议且扩展名为.jar
     *
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR file URL
     */
    public static boolean isJarFileURL(URL url) {
        return (Normal.URL_PROTOCOL_FILE.equals(url.getProtocol()) &&
                url.getPath().toLowerCase().endsWith(FileType.JAR));
    }

    /**
     * 从URL中获取流
     *
     * @param url {@link URL}
     * @return InputStream流
     */
    public static InputStream getStream(URL url) {
        Assert.notNull(url);
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 获得Reader
     *
     * @param url     {@link URL}
     * @param charset 编码
     * @return {@link BufferedReader}
     */
    public static BufferedReader getReader(URL url, Charset charset) {
        return IoUtils.getReader(getStream(url), charset);
    }

    /**
     * 从URL中获取JarFile
     *
     * @param url URL
     * @return JarFile
     */
    public static JarFile getJarFile(URL url) {
        try {
            JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
            return urlConnection.getJarFile();
        } catch (IOException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <pre>
     * 1. 多个/替换为一个
     * </pre>
     *
     * @param url URL字符串
     * @return 标准化后的URL字符串
     */
    public static String normalize(String url) {
        return normalize(url, false);
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <pre>
     * 1. 多个/替换为一个
     * </pre>
     *
     * @param url      URL字符串
     * @param isEncode 是否对URL中path部分的中文和特殊字符做转义(不包括 http:, /和域名部分)
     * @return 标准化后的URL字符串
     */
    public static String normalize(String url, boolean isEncode) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        final int sepIndex = url.indexOf("://");
        String protocol;
        String body;
        if (sepIndex > 0) {
            protocol = StringUtils.subPre(url, sepIndex + 3);
            body = StringUtils.subSuf(url, sepIndex + 3);
        } else {
            protocol = "http://";
            body = url;
        }

        final int paramsSepIndex = StringUtils.indexOf(body, Symbol.C_QUESTION_MARK);
        String params = null;
        if (paramsSepIndex > 0) {
            params = StringUtils.subSuf(body, paramsSepIndex);
            body = StringUtils.subPre(body, paramsSepIndex);
        }

        if (StringUtils.isNotEmpty(body)) {
            // 去除开头的\或者/
            //noinspection ConstantConditions
            body = body.replaceAll("^[\\\\/]+", Normal.EMPTY);
            // 替换多个\或/为单个/
            body = body.replace(Symbol.BACKSLASH, Symbol.SLASH).replaceAll("//+", Symbol.SLASH);
        }

        final int pathSepIndex = StringUtils.indexOf(body, Symbol.C_SLASH);
        String domain = body;
        String path = null;
        if (pathSepIndex > 0) {
            domain = StringUtils.subPre(body, pathSepIndex);
            path = StringUtils.subSuf(body, pathSepIndex);
        }
        if (isEncode) {
            path = encode(path);
        }
        return protocol + domain + StringUtils.nullToEmpty(path) + StringUtils.nullToEmpty(params);
    }

    /**
     * 给定的编码编码给定的URI方案
     *
     * @param scheme   要编码的方案
     * @param encoding 要编码到的字符编码
     * @return 编码方案
     */
    public static String encodeScheme(String scheme, String encoding) {
        return encode(scheme, encoding, Type.SCHEME);
    }

    /**
     * 给定的编码编码给定的URI方案
     *
     * @param scheme  要编码的方案
     * @param charset 要编码到的字符编码
     * @return 编码方案
     */
    public static String encodeScheme(String scheme, Charset charset) {
        return encode(scheme, charset, Type.SCHEME);
    }

    /**
     * 用给定的编码编码给定的URI权限.
     *
     * @param authority 要编码的权限
     * @param encoding  要编码到的字符编码
     * @return 编码信息
     */
    public static String encodeAuthority(String authority, String encoding) {
        return encode(authority, encoding, Type.AUTHORITY);
    }

    /**
     * 用给定的编码编码给定的URI权限.
     *
     * @param authority 要编码的权限
     * @param charset   要编码到的字符编码
     * @return 编码信息
     */
    public static String encodeAuthority(String authority, Charset charset) {
        return encode(authority, charset, Type.AUTHORITY);
    }

    /**
     * 用给定的编码编码给定的URI用户信息.
     *
     * @param userInfo 要编码的用户信息
     * @param encoding 要编码到的字符编码
     * @return 编码后的用户信息
     */
    public static String encodeUserInfo(String userInfo, String encoding) {
        return encode(userInfo, encoding, Type.USER_INFO);
    }

    /**
     * 用给定的编码编码给定的URI用户信息.
     *
     * @param userInfo 要编码的用户信息
     * @param charset  要编码到的字符编码
     * @return 编码后的用户信息
     */
    public static String encodeUserInfo(String userInfo, Charset charset) {
        return encode(userInfo, charset, Type.USER_INFO);
    }

    /**
     * 用给定的编码编码给定的URI主机.
     *
     * @param host     要编码的主机
     * @param encoding 要编码到的字符编码
     * @return 编码的主机
     */
    public static String encodeHost(String host, String encoding) {
        return encode(host, encoding, Type.HOST_IPV4);
    }

    /**
     * 用给定的编码编码给定的URI主机.
     *
     * @param host    要编码的主机
     * @param charset 要编码到的字符编码
     * @return 编码的主机
     */
    public static String encodeHost(String host, Charset charset) {
        return encode(host, charset, Type.HOST_IPV4);
    }

    /**
     * 用给定的编码编码给定的URI端口。
     *
     * @param port     被编码的端口
     * @param encoding 要编码到的字符编码
     * @return 编码后的端口
     */
    public static String encodePort(String port, String encoding) {
        return encode(port, encoding, Type.PORT);
    }

    /**
     * 用给定的编码编码给定的URI端口。
     *
     * @param port    被编码的端口
     * @param charset 要编码到的字符编码
     * @return 编码后的端口
     */
    public static String encodePort(String port, Charset charset) {
        return encode(port, charset, Type.PORT);
    }

    /**
     * 用给定的编码编码给定的URI路径.
     *
     * @param path     要编码的路径
     * @param encoding 要编码到的字符编码
     * @return 编码的路径
     */
    public static String encodePath(String path, String encoding) {
        return encode(path, encoding, Type.PATH);
    }

    /**
     * 用给定的编码编码给定的URI路径.
     *
     * @param path    要编码的路径
     * @param charset 要编码到的字符编码
     * @return 编码的路径
     */
    public static String encodePath(String path, Charset charset) {
        return encode(path, charset, Type.PATH);
    }

    /**
     * 用给定的编码编码给定的URI路径段.
     *
     * @param segment  要编码的段
     * @param encoding 要编码到的字符编码
     * @return 编码部分
     */
    public static String encodePathSegment(String segment, String encoding) {
        return encode(segment, encoding, Type.PATH_SEGMENT);
    }

    /**
     * 用给定的编码编码给定的URI路径段.
     *
     * @param segment 要编码的段
     * @param charset 要编码到的字符编码
     * @return 编码部分
     */
    public static String encodePathSegment(String segment, Charset charset) {
        return encode(segment, charset, Type.PATH_SEGMENT);
    }

    /**
     * 编码URL，默认使用UTF-8编码
     * 将需要转换的内容(ASCII码形式之外的内容)，用十六进制表示法转换出来，并在之前加上%开头。
     * 此方法用于POST请求中的请求体自动编码，转义大部分特殊字符
     *
     * @param url URL
     * @return 编码后的URL
     */
    public static String encodeQuery(String url) {
        return encodeQuery(url, org.aoju.bus.core.lang.Charset.UTF_8);
    }

    /**
     * 用给定的编码编码给定的URI查询.
     *
     * @param query    要编码的查询
     * @param encoding 要编码到的字符编码
     * @return 编码查询
     */
    public static String encodeQuery(String query, String encoding) {
        return encode(query, encoding, Type.QUERY);
    }

    /**
     * 用给定的编码编码给定的URI查询.
     *
     * @param query   要编码的查询
     * @param charset 要编码到的字符编码
     * @return 编码查询
     */
    public static String encodeQuery(String query, Charset charset) {
        return encode(query, charset, Type.QUERY);
    }

    /**
     * 用给定的编码编码给定的URI查询参数.
     *
     * @param queryParam 要编码的查询参数
     * @param encoding   要编码到的字符编码
     * @return 编码查询参数
     */
    public static String encodeQueryParam(String queryParam, String encoding) {

        return encode(queryParam, encoding, Type.QUERY_PARAM);
    }

    /**
     * 用给定的编码编码给定的URI查询参数.
     *
     * @param queryParam 要编码的查询参数
     * @param charset    要编码到的字符编码
     * @return 编码查询参数
     */
    public static String encodeQueryParam(String queryParam, Charset charset) {
        return encode(queryParam, charset, Type.QUERY_PARAM);
    }

    /**
     * 用给定的编码编码给定的URI片段.
     *
     * @param fragment 要编码的片段
     * @param encoding 要编码到的字符编码
     * @return 编码的字符串
     */
    public static String encodeFragment(String fragment, String encoding) {
        return encode(fragment, encoding, Type.FRAGMENT);
    }

    /**
     * 用给定的编码编码给定的URI片段.
     *
     * @param fragment 要编码的片段
     * @param charset  要编码到的字符编码
     * @return 编码的字符串
     */
    public static String encodeFragment(String fragment, Charset charset) {
        return encode(fragment, charset, Type.FRAGMENT);
    }

    /**
     * 用字符串字符串解码的变体.
     *
     * @param source   要编码的字符串
     * @param encoding 要编码到的字符编码
     * @return 编码的字符串
     */
    public static String encode(String source, String encoding) {
        return encode(source, encoding, Type.URI);
    }

    /**
     * 对URI中任何地方的所有非法字符或具有保留含义的字符进行编码.
     *
     * @param source  要编码的字符串
     * @param charset 要编码到的字符编码
     * @return 编码的字符串
     */
    public static String encode(String source, Charset charset) {
        return encode(source, charset, Type.URI);
    }

    /**
     * 应用{@link #encode(String, Charset)}到所有给定的URI变量值.
     *
     * @param uriVariables 要编码的URI变量值
     * @return 编码的字符串
     */
    public static Map<String, String> encodeUriVariables(Map<String, ?> uriVariables) {
        Map<String, String> result = new LinkedHashMap<>(uriVariables.size());
        for (Map.Entry<String, ?> entry : uriVariables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String stringValue = (value != null ? value.toString() : Normal.EMPTY);
            result.put(key, encode(stringValue, Charset.forName(org.aoju.bus.core.lang.Charset.DEFAULT_UTF_8)));
        }
        return result;
    }

    /**
     * 应用{@link #encode(String, Charset)}到所有给定的URI变量值
     *
     * @param uriVariables 要编码的URI变量值
     * @return 编码的字符串
     */
    public static Object[] encodeUriVariables(Object... uriVariables) {
        List<String> result = new ArrayList<>();
        for (Object value : uriVariables) {
            String stringValue = (value != null ? value.toString() : Normal.EMPTY);
            result.add(encode(stringValue, org.aoju.bus.core.lang.Charset.UTF_8));
        }
        return result.toArray();
    }

    private static String encode(String scheme, String encoding, Type type) {
        return encodeUriComponent(scheme, encoding, type);
    }

    private static String encode(String scheme, Charset charset, Type type) {
        return encodeUriComponent(scheme, charset, type);
    }

    /**
     * 使用给定组件指定的规则和给定的选项将给定的源编码为已编码的字符串.
     *
     * @param source   源字符串
     * @param encoding 源字符串的编码
     * @param type     源的URI组件
     * @return 编码URI
     * @throws IllegalArgumentException 当给定值不是有效的URI组件时
     */
    static String encodeUriComponent(String source, String encoding, Type type) {
        return encodeUriComponent(source, Charset.forName(encoding), type);
    }

    /**
     * 使用给定组件指定的规则和给定的选项将给定的源编码为已编码的字符串.
     *
     * @param source  源字符串
     * @param charset 源字符串的编码
     * @param type    源的URI组件
     * @return 编码URI
     * @throws IllegalArgumentException 当给定值不是有效的URI组件时
     */
    static String encodeUriComponent(String source, Charset charset, Type type) {
        if (!(source != null && source.length() > 0)) {
            return source;
        }
        if (charset == null) {
            throw new IllegalArgumentException("Charset must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        byte[] bytes;
        try {
            bytes = source.getBytes(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        boolean changed = false;
        for (byte b : bytes) {
            if (b < 0) {
                b += 256;
            }
            if (type.isAllowed(b)) {
                bos.write(b);
            } else {
                bos.write(Symbol.C_PERCENT);
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                bos.write(hex1);
                bos.write(hex2);
                changed = true;
            }
        }
        try {
            return (changed ? new String(bos.toByteArray(), charset.name()) : source);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String uriDecode(String source, Charset charset) {
        int length = source.length();
        if (length == 0) {
            return source;
        }
        if (charset == null) {
            throw new IllegalArgumentException("Charset must not be null");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;
        for (int i = 0; i < length; i++) {
            int ch = source.charAt(i);
            if (ch == Symbol.C_PERCENT) {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence " + source.substring(i));
                    }
                    bos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                } else {
                    throw new IllegalArgumentException("Invalid encoded sequence " + source.substring(i));
                }
            } else {
                bos.write(ch);
            }
        }
        try {
            return (changed ? new String(bos.toByteArray(), charset.name()) : source);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 解码URL
     * 将%开头的16进制表示的内容解码。
     *
     * @param url URL
     * @return 解码后的URL
     */
    public static String decode(String url) {
        return decode(url, org.aoju.bus.core.lang.Charset.UTF_8);
    }

    /**
     * 解码给定的URI编码组件.
     * <p>See {@link #uriDecode(String, Charset)} for the decoding rules.
     *
     * @param source   编码的字符串
     * @param encoding 要使用的字符编码
     * @return 解码值
     * @throws IllegalArgumentException 当给定源包含无效的编码序列时
     */
    public static String decode(String source, String encoding) {
        return uriDecode(source, Charset.forName(encoding));
    }

    /**
     * 解码给定的URI编码组件.
     *
     * @param source  编码的字符串
     * @param charset 要使用的字符编码
     * @return 解码值
     * @throws IllegalArgumentException 当给定源包含无效的编码序列时
     */
    public static String decode(String source, Charset charset) {
        return uriDecode(source, charset);
    }

    /**
     * 从给定的URI路径中提取文件扩展名.
     *
     * @param path URI路径 (e.g. "/products/index.html")
     * @return 提取的文件扩展名 (e.g. "html")
     */
    public static String extractFileExtension(String path) {
        int end = path.indexOf(Symbol.C_QUESTION_MARK);
        int fragmentIndex = path.indexOf(Symbol.C_SHAPE);
        if (fragmentIndex != -1 && (end == -1 || fragmentIndex < end)) {
            end = fragmentIndex;
        }
        if (end == -1) {
            end = path.length();
        }
        int begin = path.lastIndexOf(Symbol.C_SLASH, end) + 1;
        int paramIndex = path.indexOf(Symbol.C_SEMICOLON, begin);
        end = (paramIndex != -1 && paramIndex < end ? paramIndex : end);
        int extIndex = path.lastIndexOf(Symbol.C_DOT, end);
        if (extIndex != -1 && extIndex > begin) {
            return path.substring(extIndex + 1, end);
        }
        return null;
    }

    /**
     * 对URL参数做编码,只编码键和值
     * 提供的值可以是url附带参数,但是不能只是url
     *
     * <p>注意,此方法只能标准化整个URL,并不适合于单独编码参数值</p>
     *
     * @param paramsStr url参数,可以包含url本身
     * @param charset   编码
     * @return 编码后的url和参数
     */
    public static String encodeVal(String paramsStr, Charset charset) {
        if (StringUtils.isBlank(paramsStr)) {
            return Normal.EMPTY;
        }

        String urlPart = null; // url部分,不包括问号
        String paramPart; // 参数部分
        int pathEndPos = paramsStr.indexOf(Symbol.C_QUESTION_MARK);
        if (pathEndPos > -1) {
            // url + 参数
            urlPart = StringUtils.subPre(paramsStr, pathEndPos);
            paramPart = StringUtils.subSuf(paramsStr, pathEndPos + 1);
            if (StringUtils.isBlank(paramPart)) {
                // 无参数,返回url
                return urlPart;
            }
        } else {
            // 无URL
            paramPart = paramsStr;
        }

        paramPart = normalize(paramPart, charset);

        return StringUtils.isBlank(urlPart) ? paramPart : urlPart + Symbol.QUESTION_MARK + paramPart;
    }

    /**
     * 标准化参数字符串,即URL中？后的部分
     *
     * <p>注意,此方法只能标准化整个URL,并不适合于单独编码参数值</p>
     *
     * @param paramPart 参数字符串
     * @param charset   编码
     * @return 标准化的参数字符串
     */
    public static String normalize(String paramPart, Charset charset) {
        final TextUtils builder = TextUtils.create(paramPart.length() + 16);
        final int len = paramPart.length();
        String name = null;
        int pos = 0; // 未处理字符开始位置
        char c; // 当前字符
        int i; // 当前字符位置
        for (i = 0; i < len; i++) {
            c = paramPart.charAt(i);
            if (c == Symbol.C_EQUAL) { // 键值对的分界点
                if (null == name) {
                    // 只有=前未定义name时被当作键值分界符,否则做为普通字符
                    name = (pos == i) ? Normal.EMPTY : paramPart.substring(pos, i);
                    pos = i + 1;
                }
            } else if (c == Symbol.C_AND) { // 参数对的分界点
                if (pos != i) {
                    if (null == name) {
                        // 对于像&a&这类无参数值的字符串,我们将name为a的值设为""
                        name = paramPart.substring(pos, i);
                        builder.append(encodeQuery(name, charset)).append(Symbol.C_EQUAL);
                    } else {
                        builder.append(encodeQuery(name, charset)).append(Symbol.C_EQUAL).append(encodeQuery(paramPart.substring(pos, i), charset)).append(Symbol.C_AND);
                    }
                    name = null;
                }
                pos = i + 1;
            }
        }

        // 结尾处理
        if (null != name) {
            builder.append(encodeQuery(name, charset)).append(Symbol.C_EQUAL);
        }
        if (pos != i) {
            if (null == name && pos > 0) {
                builder.append(Symbol.C_EQUAL);
            }
            builder.append(encodeQuery(paramPart.substring(pos, i), charset));
        }

        // 以&结尾则去除之
        int lastIndex = builder.length() - 1;
        if (Symbol.C_AND == builder.charAt(lastIndex)) {
            builder.delTo(lastIndex);
        }
        return builder.toString();
    }

    /**
     * 将Map形式的Form表单数据转换为Url参数形式
     * paramMap中如果key为空(null和"")会被忽略,如果value为null,会被做为空白符("")
     * 会自动url编码键和值
     *
     * <pre>
     * key1=v1&amp;key2=&amp;key3=v3
     * </pre>
     *
     * @param paramMap 表单数据
     * @param charset  编码
     * @return url参数
     */
    public static String decodeMap(Map<String, ?> paramMap, Charset charset) {
        if (CollUtils.isEmpty(paramMap)) {
            return Normal.EMPTY;
        }
        if (null == charset) {// 默认编码为系统编码
            charset = org.aoju.bus.core.lang.Charset.UTF_8;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        String key;
        Object value;
        String valueStr;
        for (Map.Entry<String, ?> item : paramMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(Symbol.AND);
            }
            key = item.getKey();
            value = item.getValue();
            if (value instanceof Iterable) {
                value = CollUtils.join((Iterable<?>) value, Symbol.COMMA);
            } else if (value instanceof Iterator) {
                value = CollUtils.join((Iterator<?>) value, Symbol.COMMA);
            }
            valueStr = Convert.toString(value);
            if (StringUtils.isNotEmpty(key)) {
                sb.append(encodeAll(key, charset)).append(Symbol.EQUAL);
                if (StringUtils.isNotEmpty(valueStr)) {
                    sb.append(encodeAll(valueStr, charset));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将URL参数解析为Map(也可以解析Post中的键值对参数)
     *
     * @param params  参数字符串(或者带参数的Path)
     * @param charset 字符集
     * @return 参数Map
     */
    public static Map<String, String> decodeVal(String params, String charset) {
        final Map<String, List<String>> paramsMap = decodeObj(params, charset);
        final Map<String, String> result = MapUtils.newHashMap(paramsMap.size());
        List<String> list;
        for (Map.Entry<String, List<String>> entry : paramsMap.entrySet()) {
            list = entry.getValue();
            result.put(entry.getKey(), CollUtils.isEmpty(list) ? null : list.get(0));
        }
        return result;
    }

    /**
     * 将URL参数解析为Map(也可以解析Post中的键值对参数)
     *
     * @param params  参数字符串(或者带参数的Path)
     * @param charset 字符集
     * @return 参数Map
     */
    public static Map<String, List<String>> decodeObj(String params, String charset) {
        if (StringUtils.isBlank(params)) {
            return Collections.emptyMap();
        }

        // 去掉Path部分
        int pathEndPos = params.indexOf(Symbol.C_QUESTION_MARK);
        if (pathEndPos > -1) {
            params = StringUtils.subSuf(params, pathEndPos + 1);
        }

        final Map<String, List<String>> map = new LinkedHashMap<>();
        final int len = params.length();
        String name = null;
        int pos = 0; // 未处理字符开始位置
        int i; // 未处理字符结束位置
        char c; // 当前字符
        for (i = 0; i < len; i++) {
            c = params.charAt(i);
            if (c == Symbol.C_EQUAL) { // 键值对的分界点
                if (null == name) {
                    // name可以是""
                    name = params.substring(pos, i);
                }
                pos = i + 1;
            } else if (c == Symbol.C_AND) { // 参数对的分界点
                if (null == name && pos != i) {
                    // 对于像&a&这类无参数值的字符串,我们将name为a的值设为""
                    addParam(map, params.substring(pos, i), Normal.EMPTY, charset);
                } else if (name != null) {
                    addParam(map, name, params.substring(pos, i), charset);
                    name = null;
                }
                pos = i + 1;
            }
        }

        // 处理结尾
        if (pos != i) {
            if (name == null) {
                addParam(map, params.substring(pos, i), Normal.EMPTY, charset);
            } else {
                addParam(map, name, params.substring(pos, i), charset);
            }
        } else if (name != null) {
            addParam(map, name, Normal.EMPTY, charset);
        }

        return map;
    }

    /**
     * 将表单数据加到URL中(用于GET表单提交)
     * 表单的键值对会被url编码,但是url中原参数不会被编码
     *
     * @param url            URL
     * @param form           表单数据
     * @param charset        编码
     * @param isEncodeParams 是否对键和值做转义处理
     * @return 合成后的URL
     */
    public static String withForm(String url, Map<String, Object> form, Charset charset, boolean isEncodeParams) {
        if (isEncodeParams && StringUtils.contains(url, Symbol.C_QUESTION_MARK)) {
            url = encodeVal(url, charset);
        }

        return withForm(url, decodeMap(form, charset), charset, false);
    }

    /**
     * 将表单数据字符串加到URL中(用于GET表单提交)
     *
     * @param url         URL
     * @param queryString 表单数据字符串
     * @param charset     编码
     * @param isEncode    是否对键和值做转义处理
     * @return 拼接后的字符串
     */
    public static String withForm(String url, String queryString, Charset charset, boolean isEncode) {
        if (StringUtils.isBlank(queryString)) {
            if (StringUtils.contains(url, Symbol.C_QUESTION_MARK)) {
                return isEncode ? encodeVal(url, charset) : url;
            }
            return url;
        }

        final TextUtils textUtils = TextUtils.create(url.length() + queryString.length() + 16);
        int qmIndex = url.indexOf(Symbol.C_QUESTION_MARK);
        if (qmIndex > 0) {
            textUtils.append(isEncode ? encodeVal(url, charset) : url);
            if (false == StringUtils.endWith(url, Symbol.C_AND)) {
                textUtils.append(Symbol.C_AND);
            }
        } else {
            textUtils.append(url);
            if (qmIndex < 0) {
                textUtils.append(Symbol.C_QUESTION_MARK);
            }
        }
        textUtils.append(isEncode ? encodeVal(queryString, charset) : queryString);
        return textUtils.toString();
    }

    /**
     * 将键值对加入到值为List类型的Map中
     *
     * @param params  参数
     * @param name    key
     * @param value   value
     * @param charset 编码
     */
    private static void addParam(Map<String, List<String>> params, String name, String value, String charset) {
        name = decode(name, charset);
        value = decode(value, charset);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<>(1);
            params.put(name, values);
        }
        values.add(value);
    }

    /**
     * 获取URL中域名部分，只保留URL中的协议(Protocol)、Host，其它为null。
     *
     * @param url URL
     * @return 域名的URI
     */
    public static URI getHost(URL url) {
        if (null == url) {
            return null;
        }

        try {
            return new URI(url.getProtocol(), url.getHost(), null, null);
        } catch (URISyntaxException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 获取协议，例如http
     *
     * @return 协议，例如http
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * 设置协议，例如http
     *
     * @param scheme 协议，例如http
     * @return this
     */
    public UriUtils setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * 获取协议，例如http，如果用户未定义协议，使用默认的http协议
     *
     * @return 协议，例如http
     */
    public String getSchemeWithDefault() {
        return StringUtils.emptyToDefault(this.scheme, Http.HTTP);
    }

    /**
     * 获取 主机，例如127.0.0.1
     *
     * @return 主机，例如127.0.0.1
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机，例如127.0.0.1
     *
     * @param host 主机，例如127.0.0.1
     * @return this
     */
    public UriUtils setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取端口，默认-1
     *
     * @return 端口，默认-1
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口，默认-1
     *
     * @param port 端口，默认-1
     * @return this
     */
    public UriUtils setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * 获得authority部分
     *
     * @return authority部分
     */
    public String getAuthority() {
        return (port < 0) ? host : host + ":" + port;
    }

    /**
     * 获取路径，例如/aa/bb/cc
     *
     * @return 路径，例如/aa/bb/cc
     */
    public Path getPath() {
        return path;
    }

    /**
     * 设置路径，例如/aa/bb/cc，将覆盖之前所有的path相关设置
     *
     * @param path 路径，例如/aa/bb/cc
     * @return this
     */
    public UriUtils setPath(Path path) {
        this.path = path;
        return this;
    }

    /**
     * 获得路径，例如/aa/bb/cc
     *
     * @return 路径，例如/aa/bb/cc
     */
    public String getPathStr() {
        return null == this.path ? Symbol.SLASH : this.path.build(charset);
    }

    /**
     * 增加路径节点
     *
     * @param segment 路径节点
     * @return this
     */
    public UriUtils addPath(String segment) {
        if (StringUtils.isBlank(segment)) {
            return this;
        }
        if (null == this.path) {
            this.path = new Path();
        }
        this.path.add(segment);
        return this;
    }

    /**
     * 追加path节点
     *
     * @param segment path节点
     * @return this
     */
    public UriUtils appendPath(CharSequence segment) {
        if (StringUtils.isEmpty(segment)) {
            return this;
        }

        if (this.path == null) {
            this.path = new Path();
        }
        this.path.add(segment);
        return this;
    }

    /**
     * 获取查询语句，例如a=1&amp;b=2
     *
     * @return 查询语句，例如a=1&amp;b=2
     */
    public Query getQuery() {
        return query;
    }

    /**
     * 设置查询语句，例如a=1&amp;b=2，将覆盖之前所有的query相关设置
     *
     * @param query 查询语句，例如a=1&amp;b=2
     * @return this
     */
    public UriUtils setQuery(Query query) {
        this.query = query;
        return this;
    }

    /**
     * 获取查询语句，例如a=1&amp;b=2
     *
     * @return 查询语句，例如a=1&amp;b=2
     */
    public String getQueryStr() {
        return null == this.query ? null : this.query.build(this.charset);
    }

    /**
     * 添加查询项，支持重复键
     *
     * @param key   键
     * @param value 值
     * @return this
     */
    public UriUtils addQuery(String key, String value) {
        if (StringUtils.isEmpty(key)) {
            return this;
        }

        if (this.query == null) {
            this.query = new Query();
        }
        this.query.add(key, value);
        return this;
    }

    /**
     * 获取标识符，#后边的部分
     *
     * @return 标识符，例如#后边的部分
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * 设置标识符，例如#后边的部分
     *
     * @param fragment 标识符，例如#后边的部分
     * @return this
     */
    public UriUtils setFragment(String fragment) {
        if (StringUtils.isEmpty(fragment)) {
            this.fragment = null;
        }
        this.fragment = StringUtils.removePrefix(fragment, "#");
        return this;
    }

    /**
     * 获取标识符，#后边的部分
     *
     * @return 标识符，例如#后边的部分
     */
    public String getFragmentEncoded() {
        return encodeAll(this.fragment, this.charset);
    }

    /**
     * 获取编码，用于URLEncode和URLDecode
     *
     * @return 编码
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * 设置编码，用于URLEncode和URLDecode
     *
     * @param charset 编码
     * @return this
     */
    public UriUtils setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 创建URL字符串
     *
     * @return URL字符串
     */
    public String build() {
        return toURL().toString();
    }

    /**
     * 转换为{@link URL} 对象
     *
     * @return {@link URL}
     */
    public URL toURL() {
        return toURL(null);
    }

    /**
     * 转换为{@link URL} 对象
     *
     * @param handler {@link URLStreamHandler}，null表示默认
     * @return {@link URL}
     */
    public URL toURL(URLStreamHandler handler) {
        final StringBuilder fileBuilder = new StringBuilder();

        // path
        fileBuilder.append(StringUtils.blankToDefault(getPathStr(), Symbol.SLASH));

        // query
        final String query = getQueryStr();
        if (StringUtils.isNotBlank(query)) {
            fileBuilder.append(Symbol.C_QUESTION_MARK).append(query);
        }

        // fragment
        if (StringUtils.isNotBlank(this.fragment)) {
            fileBuilder.append(Symbol.C_SHAPE).append(getFragmentEncoded());
        }

        try {
            return new URL(getSchemeWithDefault(), host, port, fileBuilder.toString(), handler);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 转换为URI
     *
     * @return URI
     */
    public URI toURI() {
        try {
            return new URI(
                    getSchemeWithDefault(),
                    getAuthority(),
                    getPathStr(),
                    getQueryStr(),
                    getFragmentEncoded());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return build();
    }

    /**
     * 枚举，用于标识每个URI组件允许的字符.
     * 包含指示给定字符在特定URI组件中是否有效 .
     */
    public enum Type {

        SCHEME {
            @Override
            public boolean isAllowed(int c) {
                return isAlpha(c) || isDigit(c) || Symbol.C_PLUS == c || Symbol.C_HYPHEN == c || Symbol.C_DOT == c;
            }
        },
        AUTHORITY {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || Symbol.C_COLON == c || Symbol.C_AT == c;
            }
        },
        USER_INFO {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || Symbol.C_COLON == c;
            }
        },
        HOST_IPV4 {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c);
            }
        },
        HOST_IPV6 {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || Symbol.C_BRACKET_LEFT == c || Symbol.C_BRACKET_RIGHT == c || Symbol.C_COLON == c;
            }
        },
        PORT {
            @Override
            public boolean isAllowed(int c) {
                return isDigit(c);
            }
        },
        PATH {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || Symbol.C_SLASH == c;
            }
        },
        PATH_SEGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c);
            }
        },
        QUERY {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || Symbol.C_SLASH == c || Symbol.C_QUESTION_MARK == c;
            }
        },
        QUERY_PARAM {
            @Override
            public boolean isAllowed(int c) {
                if (Symbol.C_EQUAL == c || Symbol.C_AND == c) {
                    return false;
                } else {
                    return isPchar(c) || Symbol.C_SLASH == c || Symbol.C_QUESTION_MARK == c;
                }
            }
        },
        FRAGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || Symbol.C_SLASH == c || Symbol.C_QUESTION_MARK == c;
            }
        },
        URI {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c);
            }
        };

        public abstract boolean isAllowed(int c);

        protected boolean isAlpha(int c) {
            return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
        }

        protected boolean isDigit(int c) {
            return (c >= Symbol.C_ZERO && c <= Symbol.C_NINE);
        }

        protected boolean isGenericDelimiter(int c) {
            return (Symbol.C_COLON == c || Symbol.C_SLASH == c || Symbol.C_QUESTION_MARK == c || Symbol.C_SHAPE == c || Symbol.C_BRACKET_LEFT == c || Symbol.C_BRACKET_RIGHT == c || Symbol.C_AT == c);
        }

        protected boolean isSubDelimiter(int c) {
            return (Symbol.C_NOT == c || Symbol.C_DOLLAR == c || Symbol.C_AND == c || Symbol.C_SINGLE_QUOTE == c || Symbol.C_PARENTHESE_LEFT == c || Symbol.C_PARENTHESE_RIGHT == c || Symbol.C_STAR == c || Symbol.C_PLUS == c ||
                    Symbol.C_COMMA == c || Symbol.C_SEMICOLON == c || Symbol.C_EQUAL == c);
        }

        protected boolean isReserved(int c) {
            return (isGenericDelimiter(c) || isSubDelimiter(c));
        }

        protected boolean isUnreserved(int c) {
            return (isAlpha(c) || isDigit(c) || Symbol.C_HYPHEN == c || Symbol.C_DOT == c || Symbol.C_UNDERLINE == c || Symbol.C_TILDE == c);
        }

        protected boolean isPchar(int c) {
            return (isUnreserved(c) || isSubDelimiter(c) || Symbol.C_COLON == c || Symbol.C_AT == c);
        }
    }

    /**
     * URL中Path部分的封装
     */
    public static class Path {
        private List<String> segments;
        private boolean withEngTag;

        /**
         * 构建Path
         *
         * @param pathStr 初始化的路径字符串
         * @param charset decode用的编码，null表示不做decode
         * @return {@link Path}
         */
        public static Path of(String pathStr, Charset charset) {
            final Path Path = new Path();
            Path.parse(pathStr, charset);
            return Path;
        }

        /**
         * 修正节点，包括去掉前后的/，去掉空白符
         *
         * @param segment 节点
         * @return 修正后的节点
         */
        private static String fixSegment(CharSequence segment) {
            if (StringUtils.isEmpty(segment) || Symbol.SLASH.contentEquals(segment)) {
                return null;
            }

            String segmentStr = StringUtils.toString(segment);
            segmentStr = StringUtils.trim(segmentStr);
            segmentStr = StringUtils.removePrefix(segmentStr, Symbol.SLASH);
            segmentStr = StringUtils.removeSuffix(segmentStr, Symbol.SLASH);
            segmentStr = StringUtils.trim(segmentStr);
            return segmentStr;
        }

        /**
         * 是否path的末尾加/
         *
         * @param withEngTag 是否path的末尾加/
         * @return this
         */
        public Path setWithEndTag(boolean withEngTag) {
            this.withEngTag = withEngTag;
            return this;
        }

        /**
         * 获取path的节点列表
         *
         * @return 节点列表
         */
        public List<String> getSegments() {
            return this.segments;
        }

        /**
         * 获得指定节点
         *
         * @param index 节点位置
         * @return 节点，无节点或者越界返回null
         */
        public String getSegment(int index) {
            if (null == this.segments || index >= this.segments.size()) {
                return null;
            }
            return this.segments.get(index);
        }

        /**
         * 添加到path最后面
         *
         * @param segment Path节点
         * @return this
         */
        public Path add(CharSequence segment) {
            add(segment, false);
            return this;
        }

        /**
         * 添加到path最前面
         *
         * @param segment Path节点
         * @return this
         */
        public Path addBefore(CharSequence segment) {
            add(segment, true);
            return this;
        }

        /**
         * 解析path
         *
         * @param path    路径，类似于aaa/bb/ccc
         * @param charset decode编码，null表示不解码
         * @return this
         */
        public Path parse(String path, Charset charset) {
            Path Path = new Path();

            if (StringUtils.isNotEmpty(path)) {
                path = path.trim();

                // 原URL中以/结尾，则这个规则需保留
                if (StringUtils.endWith(path, Symbol.C_SLASH)) {
                    this.withEngTag = true;
                }

                final StringTokenizer tokenizer = new StringTokenizer(path, Symbol.SLASH);
                while (tokenizer.hasMoreTokens()) {
                    add(decode(tokenizer.nextToken(), charset));
                }
            }

            return Path;
        }

        /**
         * 构建path，前面带'/'
         *
         * @param charset encode编码，null表示不做encode
         * @return 如果没有任何内容，则返回空字符串""
         */
        public String build(Charset charset) {
            if (CollUtils.isEmpty(this.segments)) {
                return Normal.EMPTY;
            }

            final StringBuilder builder = new StringBuilder();
            for (String segment : segments) {
                builder.append(Symbol.C_SLASH).append(encodeAll(segment, charset));
            }
            if (withEngTag || StringUtils.isEmpty(builder)) {
                builder.append(Symbol.C_SLASH);
            }
            return builder.toString();
        }

        @Override
        public String toString() {
            return build(null);
        }

        /**
         * 增加节点
         *
         * @param segment 节点
         * @param before  是否在前面添加
         */
        private void add(CharSequence segment, boolean before) {
            final String seg = fixSegment(segment);
            if (null == seg) {
                return;
            }


            if (this.segments == null) {
                this.segments = new LinkedList<>();
            }
            if (before) {
                this.segments.add(0, seg);
            } else {
                this.segments.add(seg);
            }
        }
    }

    /**
     * URL中查询字符串部分的封装，类似于：
     * <pre>
     *   key1=v1&amp;key2=&amp;key3=v3
     * </pre>
     */
    public static class Query {

        private final TableMap<CharSequence, CharSequence> query;

        /**
         * 构造
         */
        public Query() {
            this(null);
        }

        /**
         * 构造
         *
         * @param queryMap 初始化的查询键值对
         */
        public Query(Map<? extends CharSequence, ?> queryMap) {
            if (MapUtils.isNotEmpty(queryMap)) {
                query = new TableMap<>(queryMap.size());
                addAll(queryMap);
            } else {
                query = new TableMap<>(MapUtils.DEFAULT_INITIAL_CAPACITY);
            }
        }

        /**
         * 构建Query
         *
         * @param queryMap 初始化的查询键值对
         * @return {@link Query}
         */
        public static Query of(Map<? extends CharSequence, ?> queryMap) {
            return new Query(queryMap);
        }

        /**
         * 构建Query
         *
         * @param queryStr 初始化的查询字符串
         * @param charset  decode用的编码，null表示不做decode
         * @return {@link Query}
         */
        public static Query of(String queryStr, Charset charset) {
            final Query Query = new Query();
            Query.parse(queryStr, charset);
            return Query;
        }

        /**
         * 对象转换为字符串，用于URL的Query中
         *
         * @param value 值
         * @return 字符串
         */
        private static String toStr(Object value) {
            String result;
            if (value instanceof Iterable) {
                result = CollUtils.join((Iterable<?>) value, Symbol.COMMA);
            } else if (value instanceof Iterator) {
                result = IterUtils.join((Iterator<?>) value, Symbol.COMMA);
            } else {
                result = Convert.toString(value);
            }
            return result;
        }

        /**
         * 增加键值对
         *
         * @param key   键
         * @param value 值，集合和数组转换为逗号分隔形式
         * @return this
         */
        public Query add(CharSequence key, Object value) {
            this.query.put(key, toStr(value));
            return this;
        }

        /**
         * 批量增加键值对
         *
         * @param queryMap query中的键值对
         * @return this
         */
        public Query addAll(Map<? extends CharSequence, ?> queryMap) {
            if (MapUtils.isNotEmpty(queryMap)) {
                queryMap.forEach(this::add);
            }
            return this;
        }

        /**
         * 解析URL中的查询字符串
         *
         * @param queryStr 查询字符串，类似于key1=v1&amp;key2=&amp;key3=v3
         * @param charset  decode编码，null表示不做decode
         * @return this
         */
        public Query parse(String queryStr, Charset charset) {
            if (StringUtils.isBlank(queryStr)) {
                return this;
            }

            // 去掉Path部分
            int pathEndPos = queryStr.indexOf(Symbol.C_QUESTION_MARK);
            if (pathEndPos > -1) {
                queryStr = StringUtils.subSuf(queryStr, pathEndPos + 1);
                if (StringUtils.isBlank(queryStr)) {
                    return this;
                }
            }

            final int len = queryStr.length();
            String name = null;
            int pos = 0; // 未处理字符开始位置
            int i; // 未处理字符结束位置
            char c; // 当前字符
            for (i = 0; i < len; i++) {
                c = queryStr.charAt(i);
                switch (c) {
                    case Symbol.C_EQUAL://键和值的分界符
                        if (null == name) {
                            // name可以是""
                            name = queryStr.substring(pos, i);
                            // 开始位置从分节符后开始
                            pos = i + 1;
                        }
                        // 当=不作为分界符时，按照普通字符对待
                        break;
                    case Symbol.C_AND://键值对之间的分界符
                        addParam(name, queryStr.substring(pos, i), charset);
                        name = null;
                        if (i + 4 < len && Symbol.HTML_AMP.equals(queryStr.substring(i + 1, i + 5))) {
                            //"&amp;"转义为"&"
                            i += 4;
                        }
                        // 开始位置从分节符后开始
                        pos = i + 1;
                        break;
                }
            }

            // 处理结尾
            addParam(name, queryStr.substring(pos, i), charset);
            return this;
        }

        /**
         * 获得查询的Map
         *
         * @return 查询的Map，只读
         */
        public Map<CharSequence, CharSequence> getQueryMap() {
            return MapUtils.unmodifiable(this.query);
        }

        /**
         * 获取查询值
         *
         * @param key 键
         * @return 值
         */
        public CharSequence get(CharSequence key) {
            if (MapUtils.isEmpty(this.query)) {
                return null;
            }
            return this.query.get(key);
        }

        /**
         * 构建URL查询字符串，即将key-value键值对转换为key1=v1&amp;key2=&amp;key3=v3形式
         *
         * @param charset encode编码，null表示不做encode编码
         * @return URL查询字符串
         */
        public String build(Charset charset) {
            if (MapUtils.isEmpty(this.query)) {
                return Normal.EMPTY;
            }

            final StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            CharSequence key;
            CharSequence value;
            for (Map.Entry<CharSequence, CharSequence> entry : this.query) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }
                key = entry.getKey();
                if (StringUtils.isNotEmpty(key)) {
                    sb.append(encodeAll(StringUtils.toString(key), charset)).append("=");
                    value = entry.getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        sb.append(encodeAll(StringUtils.toString(value), charset));
                    }
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return build(null);
        }

        /**
         * 将键值对加入到值为List类型的Map中,，情况如下：
         * <pre>
         *     1、key和value都不为null，类似于 "a=1"或者"=1"，直接put
         *     2、key不为null，value为null，类似于 "a="，值传""
         *     3、key为null，value不为null，类似于 "1"
         *     4、key和value都为null，忽略之，比如&&
         * </pre>
         *
         * @param key     key，为null则value作为key
         * @param value   value，为null且key不为null时传入""
         * @param charset 编码
         */
        private void addParam(String key, String value, Charset charset) {
            if (null != key) {
                final String actualKey = decode(key, charset);
                this.query.put(actualKey, StringUtils.nullToEmpty(decode(value, charset)));
            } else if (null != value) {
                // name为空，value作为name，value赋值""
                this.query.put(decode(value, charset), Normal.EMPTY);
            }
        }

    }

}
