package org.aoju.bus.gitlab.utils;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.logger.Logger;
import org.glassfish.jersey.message.MessageUtils;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class logs request and response info masking HTTP header values that are known to
 * contain sensitive information.
 * <p>
 * This class was patterned after org.glassfish.jersey.logging.LoggingInterceptor, but written in
 * such a way that it could be sub-classed and have its behavior modified.
 */
@Priority(Integer.MIN_VALUE)
public class MaskingLoggingFilter implements ClientRequestFilter, ClientResponseFilter, WriterInterceptor {

    /**
     * Default list of header names that should be masked.
     */
    public static final List<String> DEFAULT_MASKED_HEADER_NAMES =
            Collections.unmodifiableList(Arrays.asList("PRIVATE-TOKEN", "Authorization", "Proxy-Authorization"));

    /**
     * Prefix for request log entries.
     */
    protected static final String REQUEST_PREFIX = "> ";

    /**
     * Prefix for response log entries.
     */
    protected static final String RESPONSE_PREFIX = "< ";

    /**
     * Prefix that marks the beginning of a request or response section.
     */
    protected static final String SECTION_PREFIX = "- ";

    /**
     * Property name for the entity stream property
     */
    protected static final String ENTITY_STREAM_PROPERTY = MaskingLoggingFilter.class.getName() + ".entityStream";

    /**
     * Property name for the logging record id property
     */
    protected static final String LOGGING_ID_PROPERTY = MaskingLoggingFilter.class.getName() + ".id";

    protected final int maxEntitySize;
    protected final AtomicLong _id = new AtomicLong(0);
    protected Set<String> maskedHeaderNames = new HashSet<String>();

    /**
     * Creates a masking logging filter for the specified logger with entity logging disabled.
     */
    public MaskingLoggingFilter() {
        this(0, null);
    }

    /**
     * Creates a masking logging filter for the specified logger.
     *
     * @param maxEntitySize maximum number of entity bytes to be logged.  When logging if the maxEntitySize
     *                      is reached, the entity logging  will be truncated at maxEntitySize and "...more..." will be added at
     *                      the end of the log entry. If maxEntitySize is &lt;= 0, entity logging will be disabled
     */
    public MaskingLoggingFilter(final int maxEntitySize) {
        this(maxEntitySize, null);
    }

    /**
     * Creates a masking logging filter for the specified logger with entity logging disabled.
     *
     * @param maskedHeaderNames a list of header names that should have the values masked
     */
    public MaskingLoggingFilter(final List<String> maskedHeaderNames) {
        this(0, maskedHeaderNames);
    }

    /**
     * Creates a masking logging filter for the specified logger.
     *
     * @param maxEntitySize     maximum number of entity bytes to be logged.  When logging if the maxEntitySize
     *                          is reached, the entity logging  will be truncated at maxEntitySize and "...more..." will be added at
     *                          the end of the log entry. If maxEntitySize is &lt;= 0, entity logging will be disabled
     * @param maskedHeaderNames a list of header names that should have the values masked
     */
    public MaskingLoggingFilter(final int maxEntitySize, final List<String> maskedHeaderNames) {
        this.maxEntitySize = maxEntitySize;

        if (maskedHeaderNames != null) {
            maskedHeaderNames.forEach(h -> this.maskedHeaderNames.add(h.toLowerCase()));
        }
    }

    /**
     * Set the list of header names to mask values for. If null, will clear the header names to mask.
     *
     * @param maskedHeaderNames a list of header names that should have the values masked, if null, will clear
     *                          the header names to mask
     */
    public void setMaskedHeaderNames(final List<String> maskedHeaderNames) {
        this.maskedHeaderNames.clear();
        if (maskedHeaderNames != null) {
            maskedHeaderNames.forEach(h -> {
                addMaskedHeaderName(h);
            });
        }
    }

    /**
     * Add a header name to the list of masked header names.
     *
     * @param maskedHeaderName the masked header name to add
     */
    public void addMaskedHeaderName(String maskedHeaderName) {
        if (maskedHeaderName != null) {
            maskedHeaderName = maskedHeaderName.trim();
            if (maskedHeaderName.length() > 0) {
                maskedHeaderNames.add(maskedHeaderName.toLowerCase());
            }
        }
    }

    protected void log(final StringBuilder sb) {
        Logger.info(sb.toString());
    }

    protected StringBuilder appendId(final StringBuilder sb, final long id) {
        sb.append(id).append(Symbol.C_SPACE);
        return (sb);
    }

    protected void printRequestLine(final StringBuilder sb, final String note, final long id, final String method, final URI uri) {
        appendId(sb, id).append(SECTION_PREFIX)
                .append(note)
                .append(" on thread ").append(Thread.currentThread().getName())
                .append(Symbol.C_LF);
        appendId(sb, id).append(REQUEST_PREFIX).append(method).append(Symbol.C_SPACE)
                .append(uri.toASCIIString()).append(Symbol.C_LF);
    }

    protected void printResponseLine(final StringBuilder sb, final String note, final long id, final int status) {
        appendId(sb, id).append(SECTION_PREFIX)
                .append(note)
                .append(" on thread ").append(Thread.currentThread().getName()).append(Symbol.C_LF);
        appendId(sb, id).append(RESPONSE_PREFIX)
                .append(status)
                .append(Symbol.C_LF);
    }

    protected Set<Entry<String, List<String>>> getSortedHeaders(final Set<Entry<String, List<String>>> headers) {
        final TreeSet<Entry<String, List<String>>> sortedHeaders = new TreeSet<Entry<String, List<String>>>(
                (Entry<String, List<String>> o1, Entry<String, List<String>> o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    /**
     * Logs each of the HTTP headers, masking the value of the header if the header key is
     * in the list of masked header names.
     *
     * @param sb      the StringBuilder to build up the logging info in
     * @param id      the ID for the logging line
     * @param prefix  the logging line prefix character
     * @param headers a MultiValue map holding the header keys and values
     */
    protected void printHeaders(final StringBuilder sb,
                                final long id,
                                final String prefix,
                                final MultivaluedMap<String, String> headers) {

        getSortedHeaders(headers.entrySet()).forEach(h -> {

            final List<?> values = h.getValue();
            final String header = h.getKey();
            final boolean isMaskedHeader = maskedHeaderNames.contains(header.toLowerCase());

            if (values.size() == 1) {
                String value = (isMaskedHeader ? "********" : values.get(0).toString());
                appendId(sb, id).append(prefix).append(header).append(": ").append(value).append(Symbol.C_LF);
            } else {

                final StringBuilder headerBuf = new StringBuilder();
                for (final Object value : values) {
                    if (headerBuf.length() == 0) {
                        headerBuf.append(", ");
                    }

                    headerBuf.append(isMaskedHeader ? "********" : value.toString());
                }

                appendId(sb, id).append(prefix).append(header).append(": ").append(headerBuf.toString()).append(Symbol.C_LF);
            }
        });
    }

    protected void buildEntityLogString(StringBuilder sb, byte[] entity, int entitySize, Charset charset) {

        sb.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
        if (entitySize > maxEntitySize) {
            sb.append("...more...");
        }

        sb.append(Symbol.C_LF);
    }

    private InputStream logResponseEntity(final StringBuilder sb, InputStream stream, final Charset charset) throws IOException {

        if (maxEntitySize <= 0) {
            return (stream);
        }

        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        buildEntityLogString(sb, entity, entitySize, charset);
        stream.reset();
        return stream;
    }

    @Override
    public void filter(ClientRequestContext requestContext) {

        final long id = _id.incrementAndGet();
        requestContext.setProperty(LOGGING_ID_PROPERTY, id);

        final StringBuilder sb = new StringBuilder();
        printRequestLine(sb, "Sending client request", id, requestContext.getMethod(), requestContext.getUri());
        printHeaders(sb, id, REQUEST_PREFIX, requestContext.getStringHeaders());

        if (requestContext.hasEntity() && maxEntitySize > 0) {
            final OutputStream stream = new LoggingStream(sb, requestContext.getEntityStream());
            requestContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_STREAM_PROPERTY, stream);
        } else {
            log(sb);
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        final Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
        final long id = requestId != null ? (Long) requestId : _id.incrementAndGet();

        final StringBuilder sb = new StringBuilder();
        printResponseLine(sb, "Received server response", id, responseContext.getStatus());
        printHeaders(sb, id, RESPONSE_PREFIX, responseContext.getHeaders());

        if (responseContext.hasEntity() && maxEntitySize > 0) {
            responseContext.setEntityStream(logResponseEntity(sb, responseContext.getEntityStream(),
                    MessageUtils.getCharset(responseContext.getMediaType())));
        }

        log(sb);
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {

        final LoggingStream stream = (LoggingStream) context.getProperty(ENTITY_STREAM_PROPERTY);
        context.proceed();
        if (stream == null) {
            return;
        }

        MediaType mediaType = context.getMediaType();
        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE) ||
                mediaType.isCompatible(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
            log(stream.getStringBuilder(MessageUtils.getCharset(mediaType)));
        }

    }

    /**
     * This class is responsible for logging the request entities, it will truncate at maxEntitySize
     * and add "...more..." to the end of the entity log string.
     */
    protected class LoggingStream extends FilterOutputStream {

        private final StringBuilder sb;
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        LoggingStream(StringBuilder sb, OutputStream out) {
            super(out);
            this.sb = sb;
        }

        StringBuilder getStringBuilder(Charset charset) {
            final byte[] entity = outputStream.toByteArray();
            buildEntityLogString(sb, entity, entity.length, charset);
            return (sb);
        }

        @Override
        public void write(final int i) throws IOException {

            if (outputStream.size() <= maxEntitySize) {
                outputStream.write(i);
            }

            out.write(i);
        }
    }
}
