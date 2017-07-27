package org.openlearn.web.rest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlearn.OpenLearnApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import springfox.documentation.staticdocs.SwaggerResultHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("swagger,s2m")
public class Swagger2MarkupIntTest {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/asciidoc/snippets");

	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(documentationConfiguration(restDocumentation))        .build();
	}

	@Test
	public void getAllUsersSamples() throws Exception {
		TestUtil.setSecurityContextAdmin();
		mockMvc.perform(get("/api/users")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(document("getAllUsersUsingGET", preprocessResponse(prettyPrint())))
		.andExpect(status().isOk());
	}
	@Test
	public void convertSwaggerToAsciiDoc() throws Exception {
		mockMvc.perform(get("/v2/api-docs")
				.accept(MediaType.APPLICATION_JSON))
		.andDo(SwaggerResultHandler.outputDirectory("build/swagger")
				.build())
		.andExpect(status().isOk());
	}
}
