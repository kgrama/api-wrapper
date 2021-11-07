package com.github.kgrama.apiwrapperdemo.api.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;

import com.github.kgrama.apiwrapperdemo.api.LookupExternalData;
import com.github.kgrama.apiwrapperdemo.support.MultipartDataTestParent;

import lombok.extern.log4j.Log4j2;

@Log4j2
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class APIWrapperExternalAPITest extends MultipartDataTestParent {

	private String urlString = "";
	private String  testPath = "/some-path/";

	private String[] validATMIdentifiers = {"LFFFBC11", "LFFADC11"};
	private String[] invalidATMIdentifiers = {"30935500", "30847300"};
	
	Base64.Encoder encoder = Base64.getUrlEncoder();

	@BeforeEach
	public void initUrlString() {
		urlString = String.format("http://localhost:%s%s", mockBackend.getPort(), testPath);
	}
	
	@Autowired
    private MockMvc mvc;
	
	@Test
	public void verifyOKResponse() throws Exception {
		log.debug("Verify that status !2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var lookUpReq = "/"+ LookupExternalData.V1_PATH.concat(encoder.encodeToString(urlString.getBytes())).concat("/").concat(validATMIdentifiers[0]);
		mvc.perform(get(lookUpReq)).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void verifyNotFoundResponse() throws Exception {
		log.debug("Verify that status !2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var lookUpReq = "/"+ LookupExternalData.V1_PATH.concat(encoder.encodeToString(urlString.getBytes())).concat("/").concat(invalidATMIdentifiers[0]);
		mvc.perform(get(lookUpReq)).andDo(print()).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void verifyInvalidRequestResponseA() throws Exception {
		log.debug("Verify that status !2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var lookUpReq = "/"+ LookupExternalData.V1_PATH.concat(encoder.encodeToString(urlString.getBytes()).concat("blah")).concat("/").concat(invalidATMIdentifiers[0]);
		mvc.perform(get(lookUpReq)).andDo(print()).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void verifyInvalidRequestResponseB() throws Exception {
		log.debug("Verify that status !2xx responses are handled");
		mockBackend.enqueue(initHttpOKMockResponse().setBodyDelay(2, TimeUnit.SECONDS));
		var lookUpReq = "/"+ LookupExternalData.V1_PATH.concat(encoder.encodeToString(urlString.getBytes())).concat("/");
		mvc.perform(get(lookUpReq)).andDo(print()).andExpect(status().is4xxClientError());
	}
}
