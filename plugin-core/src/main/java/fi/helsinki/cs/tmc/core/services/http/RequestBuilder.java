package fi.helsinki.cs.tmc.core.services.http;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Class that builds various HTTP requests and uses RequestExecutor to execute
 * the request.
 */
class RequestBuilder {

    private UsernamePasswordCredentials credentials;
    private final RequestExecutorFactory factory;

    /**
     * Constructor.
     *
     * @param factory
     *            Factory that will be used to create RequestExecutors
     *            internally
     */
    RequestBuilder(final RequestExecutorFactory factory) {

        this.factory = factory;
    }

    /**
     * Sets the credentials.
     *
     * @param username
     *            Username
     * @param password
     *            Password
     * @return returns itself to enable builder pattern
     */
    RequestBuilder setCredentials(final String username, final String password) {

        credentials = new UsernamePasswordCredentials(username, password);
        return this;
    }

    /**
     * Helper method for executor creation.
     *
     * @param url
     *            URL to be used
     * @return Created RequestExecutor
     */
    private RequestExecutor createExecutor(final String url) {

        return factory.createExecutor(url, credentials);
    }

    /**
     * Helper method for executor creation.
     *
     * @param request
     *            Request to be used
     * @return Created RequestExecutor
     */
    private RequestExecutor createExecutor(final HttpPost request) {

        return factory.createExecutor(request, credentials);
    }

    /**
     * Returns binary output from given URL.
     *
     * @param url
     *            URL where data will be downloaded
     * @return Byte array with the requested data
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */

    public byte[] getForBinary(final String url) throws Exception {

        return downloadToBinary(createExecutor(url));
    }

    /**
     * Returns text output from given URL.
     *
     * @param url
     *            URL where the text will be fetched
     * @return The fetched text
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public String getForText(final String url) throws Exception {

        return downloadToText(createExecutor(url));
    }

    /**
     * Makes a HTTP Post request and return byte array from the URL. Parameters
     * will be UTF-8 encoded.
     *
     * @param url
     *            URL where it will be posted
     * @param params
     *            params that will be posted
     * @return The byte array that was downloaded
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public byte[] postForBinary(final String url, final Map<String, String> params) throws Exception {

        return downloadToBinary(createExecutor(makePostRequest(url, params)));
    }

    /**
     * Makes a HTTP Post request and returns text from the URL. Parameters will
     * be UTF-8 encoded.
     *
     * @param url
     *            URL where request will be posted
     * @param params
     *            Params that will be posted
     * @return Text that was received from the URl
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public String postForText(final String url, final Map<String, String> params) throws Exception {

        return downloadToText(createExecutor(makePostRequest(url, params)));
    }

    /**
     * Makes a raw HTTP post request where data won't be touched in any way.
     *
     * @param url
     *            URL where data will be posted
     * @param data
     *            data to be posted
     * @return Text returned from the post request
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public String rawPostForText(final String url, final byte[] data) throws Exception {

        return downloadToText(createExecutor(makeRawPostRequest(url, data)));
    }

    /**
     * Makes a raw HTTP post request with extra headers where data won't be
     * touched in any way.
     *
     * @param url
     *            URL where data will be posted
     * @param data
     *            data to be posted
     * @param extraHeaders
     *            extra headers to be used
     * @return Text returned from the post request
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public String rawPostForText(final String url, final byte[] data, final Map<String, String> extraHeaders) throws Exception {

        return downloadToText(createExecutor(makeRawPostRequest(url, data, extraHeaders)));
    }

    /**
     * Uploads a file to server and returns a text response.
     *
     * @param url
     *            URL where the file will be uploaded
     * @param params
     *            Parameters that will be used
     * @param fileField
     *            Name of the file in the header field
     * @param data
     *            Data of the file in the header file
     * @return Text response from the server
     * @throws Exception
     *             Throws various exceptions, for example when credentials are
     *             wrong or when IO exception happens
     */
    public String uploadFileForTextDownload(final String url, 
                                            final Map<String, String> params, 
                                            final String fileField, 
                                            final byte[] data) throws Exception {

        final HttpPost request = makeFileUploadRequest(url, params, fileField, data);
        return downloadToText(createExecutor(request));
    }

    private byte[] downloadToBinary(final RequestExecutor download) throws Exception {

        return EntityUtils.toByteArray(download.execute());
    }

    private String downloadToText(final RequestExecutor download) throws Exception {

        return EntityUtils.toString(download.execute(), "UTF-8");
    }

    private HttpPost makePostRequest(final String url, final Map<String, String> params) throws URISyntaxException {

        final HttpPost request = new HttpPost(url);

        final List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
        for (final Map.Entry<String, String> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
        }

        try {
            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
            request.setEntity(entity);
            return request;
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private HttpPost makeRawPostRequest(final String url, final byte[] data) throws URISyntaxException {

        final Map<String, String> empty = Collections.emptyMap();
        return makeRawPostRequest(url, data, empty);
    }

    private HttpPost makeRawPostRequest(final String url, final byte[] data, final Map<String, String> extraHeaders) throws URISyntaxException {

        final HttpPost request = new HttpPost(url);
        for (final Map.Entry<String, String> header : extraHeaders.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }

        final ByteArrayEntity entity = new ByteArrayEntity(data);
        request.setEntity(entity);
        return request;
    }

    private HttpPost makeFileUploadRequest(final String url, 
                                           final Map<String, String> params, 
                                           final String fileField, 
                                           final byte[] data) throws URISyntaxException {

        final HttpPost request = new HttpPost(url);

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));

        for (final Map.Entry<String, String> e : params.entrySet()) {
            builder.addTextBody(e.getKey(), e.getValue(), ContentType.TEXT_PLAIN.withCharset(Charsets.UTF_8));
        }

        builder.addBinaryBody(fileField, data, ContentType.APPLICATION_OCTET_STREAM, "file");
        request.setEntity(builder.build());

        return request;
    }
}
