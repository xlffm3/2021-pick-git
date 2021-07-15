package com.woowacourse.pickgit.post.application;

import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class PostFactory {

    private PostFactory() {
    }

    public static List<PostRequestDto> mockPostRequestDtos() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        return List.of(
            new PostRequestDto("a", "sean", images,
                "atdd-subway-fare", List.of("Java", "Python", "C++"),
                "woowacourse mission"),
            new PostRequestDto("a", "ginger", images,
                "jwp-chess", List.of("Javascirpt", "C", "HTML"),
                "it's so easy!"),
            new PostRequestDto("a", "dani", images,
                "java-racingcar", List.of("Go", "Objective-C"),
                "I love TDD"),
            new PostRequestDto("a", "coda", images,
                "junit-test", List.of("Java", "CSS", "HTML"),
                "hi there!"),
            new PostRequestDto("a", "dave", images,
                "jpa-learning-teest", List.of("Java", "CSS", "HTML"),
                "jpa is so fun!")
        );
    }

    public static List<User> mockUsers() {
        return List.of(
            new User(new BasicProfile("sean", "a.jpg", "a"),
                new GithubProfile("github1.com", "a", "a", "a", "a")),
            new User(new BasicProfile("ginger", "a.jpg", "a"),
                new GithubProfile("github2.com", "a", "a", "a", "a")),
            new User(new BasicProfile("dani", "a.jpg", "a"),
                new GithubProfile("dani.com", "a", "a", "a", "a")),
            new User(new BasicProfile("coda", "a.jpg", "a"),
                new GithubProfile("coda.com", "a", "a", "a", "a")),
            new User(new BasicProfile("dave", "a.jpg", "a"),
                new GithubProfile("dave.com", "a", "a", "a", "a"))
        );
    }
}
