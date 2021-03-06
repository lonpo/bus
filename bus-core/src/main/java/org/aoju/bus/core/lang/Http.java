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
package org.aoju.bus.core.lang;

/**
 * HTTP 相关常量
 *
 * @author Kimi Liu
 * @version 5.9.1
 * @since JDK 1.8+
 */
public class Http {

    /**
     * The http
     */
    public static final String HTTP = "http";
    /**
     * The https
     */
    public static final String HTTPS = "https";
    /**
     * The prefix http
     */
    public static final String HTTP_PREFIX = HTTP + Symbol.COLON + Symbol.FORWARDSLASH;
    /**
     * The prefix https
     */
    public static final String HTTPS_PREFIX = HTTPS + Symbol.COLON + Symbol.FORWARDSLASH;
    /**
     * HTTP Method GET
     */
    public static final String GET = "GET";
    /**
     * HTTP Method POST
     */
    public static final String POST = "POST";
    /**
     * HTTP Method PUT
     */
    public static final String PUT = "PUT";
    /**
     * HTTP Method PATCH
     */
    public static final String PATCH = "PATCH";
    /**
     * HTTP Method DELETE
     */
    public static final String DELETE = "DELETE";
    /**
     * HTTP Method HEAD
     */
    public static final String HEAD = "HEAD";
    /**
     * HTTP Method TRACE
     */
    public static final String TRACE = "TRACE";
    /**
     * HTTP Method CONNECT
     */
    public static final String CONNECT = "CONNECT";
    /**
     * HTTP Method OPTIONS
     */
    public static final String OPTIONS = "OPTIONS";
    /**
     * HTTP Method BEFORE
     */
    public static final String BEFORE = "BEFORE";
    /**
     * HTTP Method AFTER
     */
    public static final String AFTER = "AFTER";
    /**
     * HTTP Method MOVE
     */
    public static final String MOVE = "MOVE";
    /**
     * HTTP Method PROPPATCH
     */
    public static final String PROPPATCH = "PROPPATCH";
    /**
     * HTTP Method REPORT
     */
    public static final String REPORT = "REPORT";
    /**
     * HTTP Method PROPFIND
     */
    public static final String PROPFIND = "PROPFIND";
    /**
     * HTTP Method ALL
     */
    public static final String ALL = "ALL";
    /**
     * The use status
     */
    public static final String RESPONSE_STATUS_UTF8 = ":status";
    /**
     * The use method
     */
    public static final String TARGET_METHOD_UTF8 = ":method";
    /**
     * The use path
     */
    public static final String TARGET_PATH_UTF8 = ":path";
    /**
     * The use scheme
     */
    public static final String TARGET_SCHEME_UTF8 = ":scheme";
    /**
     * The use authority
     */
    public static final String TARGET_AUTHORITY_UTF8 = ":authority";
    /**
     * The IPV4 127.0.0.1
     */
    public static final String HTTP_HOST_IPV4 = "127.0.0.1";
    /**
     * The localhost
     */
    public static final String HTTP_HOST_LOCAL = "localhost";
    /**
     * HTTP Status-Code 100: Continue.
     */
    public static final int HTTP_CONTINUE = 100;
    /**
     * HTTP Status-Code 101: Switching Protocols.
     */
    public static final int HTTP_SWITCHING_PROTOCOL = 101;
    /**
     * HTTP Status-Code 200: OK.
     */
    public static final int HTTP_OK = 200;
    /**
     * HTTP Status-Code 201: Created.
     */
    public static final int HTTP_CREATED = 201;
    /**
     * HTTP Status-Code 202: Accepted.
     */
    public static final int HTTP_ACCEPTED = 202;
    /**
     * HTTP Status-Code 203: Non-Authoritative Information.
     */
    public static final int HTTP_NOT_AUTHORITATIVE = 203;
    /**
     * HTTP Status-Code 204: No Content.
     */
    public static final int HTTP_NO_CONTENT = 204;
    /**
     * HTTP Status-Code 205: Reset Content.
     */
    public static final int HTTP_RESET = 205;
    /**
     * HTTP Status-Code 206: Partial Content.
     */
    public static final int HTTP_PARTIAL = 206;
    /**
     * HTTP Status-Code 300: Multiple Choices.
     */
    public static final int HTTP_MULT_CHOICE = 300;
    /**
     * HTTP Status-Code 301: Moved Permanently.
     */
    public static final int HTTP_MOVED_PERM = 301;
    /**
     * HTTP Status-Code 302: Temporary Redirect.
     */
    public static final int HTTP_MOVED_TEMP = 302;
    /**
     * HTTP Status-Code 303: See Other.
     */
    public static final int HTTP_SEE_OTHER = 303;
    /**
     * HTTP Status-Code 304: Not Modified.
     */
    public static final int HTTP_NOT_MODIFIED = 304;
    /**
     * HTTP Status-Code 305: Use Proxy.
     */
    public static final int HTTP_USE_PROXY = 305;
    /**
     * HTTP Status-Code 307: Temporary Redirect.
     */
    public static final int HTTP_TEMP_REDIRECT = 307;
    /**
     * HTTP Status-Code 308: Use perm Redirect.
     */
    public static final int HTTP_PERM_REDIRECT = 308;
    /**
     * HTTP Status-Code 400: Bad Request.
     */
    public static final int HTTP_BAD_REQUEST = 400;
    /**
     * HTTP Status-Code 401: Unauthorized.
     */
    public static final int HTTP_UNAUTHORIZED = 401;
    /**
     * HTTP Status-Code 402: Payment Required.
     */
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    /**
     * HTTP Status-Code 403: Forbidden.
     */
    public static final int HTTP_FORBIDDEN = 403;
    /**
     * HTTP Status-Code 404: Not Found.
     */
    public static final int HTTP_NOT_FOUND = 404;
    /**
     * HTTP Status-Code 405: Method Not Allowed.
     */
    public static final int HTTP_BAD_METHOD = 405;
    /**
     * HTTP Status-Code 406: Not Acceptable.
     */
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    /**
     * HTTP Status-Code 407: Proxy Authentication Required.
     */
    public static final int HTTP_PROXY_AUTH = 407;
    /**
     * HTTP Status-Code 408: Request Time-Out.
     */
    public static final int HTTP_CLIENT_TIMEOUT = 408;
    /**
     * HTTP Status-Code 409: Conflict.
     */
    public static final int HTTP_CONFLICT = 409;
    /**
     * HTTP Status-Code 410: Gone.
     */
    public static final int HTTP_GONE = 410;
    /**
     * HTTP Status-Code 411: Length Required.
     */
    public static final int HTTP_LENGTH_REQUIRED = 411;
    /**
     * HTTP Status-Code 412: Precondition Failed.
     */
    public static final int HTTP_PRECON_FAILED = 412;
    /**
     * HTTP Status-Code 413: Request Entity Too Large.
     */
    public static final int HTTP_ENTITY_TOO_LARGE = 413;
    /**
     * HTTP Status-Code 414: Request-URI Too Large.
     */
    public static final int HTTP_REQ_TOO_LONG = 414;
    /**
     * HTTP Status-Code 415: Unsupported Media Type.
     */
    public static final int HTTP_UNSUPPORTED_TYPE = 415;
    /**
     * HTTP Status-Code 500: Internal Server Error.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;
    /**
     * HTTP Status-Code 501: Not Implemented.
     */
    public static final int HTTP_NOT_IMPLEMENTED = 501;
    /**
     * HTTP Status-Code 502: Bad Gateway.
     */
    public static final int HTTP_BAD_GATEWAY = 502;
    /**
     * HTTP Status-Code 503: Service Unavailable.
     */
    public static final int HTTP_UNAVAILABLE = 503;
    /**
     * HTTP Status-Code 504: Gateway Timeout.
     */
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    /**
     * HTTP Status-Code 505: HTTP Version Not Supported.
     */
    public static final int HTTP_VERSION = 505;
    /**
     * From the HTTP/2 specs, the default initial window size for all streams is 64 KiB. (Chrome 25
     * uses 10 MiB).
     */
    public static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
    /**
     * HTTP/2: Size in bytes of the table used to decode the sender's header blocks.
     */
    public static final int HEADER_TABLE_SIZE = 1;
    /**
     * HTTP/2: The peer must not send a PUSH_PROMISE frame when this is 0.
     */
    public static final int ENABLE_PUSH = 2;
    /**
     * Sender's maximum number of concurrent streams.
     */
    public static final int MAX_CONCURRENT_STREAMS = 4;
    /**
     * HTTP/2: Size in bytes of the largest frame payload the sender will accept.
     */
    public static final int MAX_FRAME_SIZE = 5;
    /**
     * HTTP/2: Advisory only. Size in bytes of the largest header list the sender will accept.
     */
    public static final int MAX_HEADER_LIST_SIZE = 6;
    /**
     * Window size in bytes.
     */
    public static final int INITIAL_WINDOW_SIZE = 7;

}
