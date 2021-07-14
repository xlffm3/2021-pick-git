package com.woowacourse.pickgit.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.domain.OAuthClient;
import com.woowacourse.pickgit.authentication.presentation.dto.OAuthTokenResponse;
import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.config.StorageConfiguration;
import com.woowacourse.pickgit.post.presentation.dto.PostRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Import(StorageConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PostAcceptanceTest {

    @LocalServerPort
    int port;

    @MockBean
    private OAuthClient oAuthClient;

    private List<MultipartFile> images;
    private String githubRepoUrl;
    private List<String> tags;
    private String content;

    private Map<String, Object> request;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        images = List.of(
            FileFactory.getTestImage1(),
            FileFactory.getTestImage2()
        );
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git";
        tags = List.of("java", "spring");
        content = "this is content";

        Map<String, Object> body = new HashMap<>();
        body.put("githubRepoUrl", githubRepoUrl);
        body.put("tags", tags);
        body.put("content", content);
        request = body;
    }

    @DisplayName("사용자는 게시글을 등록한다.")
    @Test
    void write_LoginUser_Success() {
        // given
        String token = 로그인_되어있음().getToken();

        // when
        RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 유효하지 않은 토큰이 있는 경우 (Authorization header O)")
    @Test
    void write_GuestUserWithToken_Fail() {
        // given
        String token = "Bearer guest";

        // when
        RestAssured
            .given().log().all()
            .auth().oauth2(token)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .extract();
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 토큰이 없는 경우 (Authorization header X)")
    @Test
    void write_GuestUserWithoutToken_Fail() {
        // when
        RestAssured
            .given().log().all()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(request)
            .multiPart("images", FileFactory.getTestImage1File())
            .multiPart("images", FileFactory.getTestImage2File())
            .when()
            .post("/api/posts")
            .then().log().all()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .extract();
    }

    private OAuthTokenResponse 로그인_되어있음() {
        OAuthTokenResponse response = 로그인_요청().as(OAuthTokenResponse.class);
        assertThat(response.getToken()).isNotBlank();
        return response;
    }

    private ExtractableResponse<Response> 로그인_요청() {
        // given
        String oauthCode = "1234";
        String accessToken = "oauth.access.token";

        OAuthProfileResponse oAuthProfileResponse = new OAuthProfileResponse(
            "pick-git-login", "image", "hi~", "github.com/",
            null, null, null, null
        );

        // mock
        when(oAuthClient.getAccessToken(oauthCode)).thenReturn(accessToken);
        when(oAuthClient.getGithubProfile(accessToken)).thenReturn(oAuthProfileResponse);

        // when
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code=" + oauthCode)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
