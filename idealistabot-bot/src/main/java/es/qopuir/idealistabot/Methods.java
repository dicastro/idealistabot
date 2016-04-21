package es.qopuir.idealistabot;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import es.qopuir.telegrambot.model.ForceReply;
import es.qopuir.telegrambot.model.ReplyKeyboardHide;
import es.qopuir.telegrambot.model.ReplyKeyboardMarkup;
import es.qopuir.telegrambot.model.response.MessageResponse;
import es.qopuir.telegrambot.model.response.UpdateResponse;
import es.qopuir.telegrambot.model.response.UserResponse;

@Component
public class Methods {
	private static final Logger LOG = LoggerFactory.getLogger(Methods.class);
	
    private String apiBaseUrl;
    private RestTemplate restTemplate;

    @Autowired
    public Methods(BotProperties botProperties) {
        apiBaseUrl = "https://api.telegram.org/bot" + botProperties.getApiKey() + "/";
    }

    protected RestTemplate getRestTemplate() {
        if (null == restTemplate) {
            restTemplate = new RestTemplate();
        }

        return restTemplate;
    }

    protected URI getSendMessageURI() {
        return URI.create(apiBaseUrl + "sendMessage");
    }

    protected URI getSendPhotoURI() {
        return URI.create(apiBaseUrl + "sendPhoto");
    }

    protected URI getUpdatesURI() {
        return URI.create(apiBaseUrl + "getUpdates");
    }

    public UserResponse getMe() {
        return getRestTemplate().getForObject(URI.create(apiBaseUrl + "getMe"), UserResponse.class);
    }

    public UpdateResponse getUpdates() {
        return getRestTemplate().getForObject(getUpdatesURI(), UpdateResponse.class);
    }

    public MessageResponse sendMessage(int chatId, String text) {
        return sendMessageInternal(chatId, text, false, 0, null);
    }

    public MessageResponse sendMessage(int chatId, String text, boolean disableWebPagePreview, int replyToMessageId,
            ReplyKeyboardMarkup replyMarkup) {
        return sendMessageInternal(chatId, text, disableWebPagePreview, replyToMessageId, replyMarkup);
    }

    public MessageResponse sendMessage(int chatId, String text, boolean disableWebPagePreview, int replyToMessageId, ReplyKeyboardHide replyMarkup) {
        return sendMessageInternal(chatId, text, disableWebPagePreview, replyToMessageId, replyMarkup);
    }

    public MessageResponse sendMessage(int chatId, String text, boolean disableWebPagePreview, int replyToMessageId, ForceReply replyMarkup) {
        return sendMessageInternal(chatId, text, disableWebPagePreview, replyToMessageId, replyMarkup);
    }

    private MessageResponse sendMessageInternal(int chatId, String text, boolean disableWebPagePreview, int replyToMessageId, Object replyMarkup) {
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();

        data.add("chat_id", chatId);
        data.add("text", text);
        data.add("disable_web_page_preview", disableWebPagePreview);

        if (replyToMessageId != 0) {
            data.add("reply_to_message_id", replyToMessageId);
        }

        if (replyMarkup != null) {
            data.add("reply_markup", replyMarkup);
        }

        try {
        	return getRestTemplate().postForObject(getSendMessageURI(), data, MessageResponse.class);
        } catch (Exception e) {
        	LOG.error("Error sending message", e);
        	
        	return null;
        }
    }

    public MessageResponse sendPhoto(int chatId, URL imageUrl, File tempFile) throws IOException {
        return sendPhotoInternal(chatId, imageUrl, tempFile, null, 0, null);
    }

    public MessageResponse sendPhoto(int chatId, URL imageUrl, File tempFile, String caption) throws IOException {
        return sendPhotoInternal(chatId, imageUrl, tempFile, caption, 0, null);
    }

    public MessageResponse sendPhoto(int chatId, URL imageUrl, File tempFile, String caption, int replyToMessageId, ReplyKeyboardMarkup replyMarkup)
            throws IOException {
        return sendPhotoInternal(chatId, imageUrl, tempFile, caption, replyToMessageId, replyMarkup);
    }

    public MessageResponse sendPhoto(int chatId, URL imageUrl, File tempFile, String caption, int replyToMessageId, ReplyKeyboardHide replyMarkup)
            throws IOException {
        return sendPhotoInternal(chatId, imageUrl, tempFile, caption, replyToMessageId, replyMarkup);
    }

    public MessageResponse sendPhoto(int chatId, URL imageUrl, File tempFile, String caption, int replyToMessageId, ForceReply replyMarkup)
            throws IOException {
        return sendPhotoInternal(chatId, imageUrl, tempFile, caption, replyToMessageId, replyMarkup);
    }

    private MessageResponse sendPhotoInternal(int chatId, URL imageUrl, File tempFile, String caption, int replyToMessageId, Object replyMarkup)
            throws IOException {
        HttpEntity<Object> request = createImageRequest(chatId, imageUrl, tempFile, caption, replyToMessageId, replyMarkup);

        return getRestTemplate().postForObject(getSendPhotoURI(), request, MessageResponse.class);
    }

    private HttpEntity<Object> createImageRequest(int chatId, URL imageUrl, File tempFile, String caption, int replyToMessageId, Object replyMarkup)
            throws IOException {
        Files.copy(imageUrl.openStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Resource photoResource = new FileSystemResource(tempFile);

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        
        data.add("chat_id", chatId);
        data.add("photo", photoResource);
        data.add("caption", caption);
        
        if (replyToMessageId != 0) {
            data.add("reply_to_message_id", replyToMessageId);
        }
        
        if (replyMarkup != null) {
            data.add("reply_markup", replyMarkup);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        return new HttpEntity<Object>(data, headers);
    }
}