package com.woowacourse.pickgit.user.infrastructure.calculator;

import com.woowacourse.pickgit.user.domain.Contribution;
import com.woowacourse.pickgit.user.domain.PlatformContributionCalculator;
import com.woowacourse.pickgit.user.domain.PlatformContributionExtractor;
import com.woowacourse.pickgit.user.infrastructure.dto.CountDto;
import com.woowacourse.pickgit.user.infrastructure.dto.StarsDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GithubContributionCalculator implements PlatformContributionCalculator {

    private final PlatformContributionExtractor platformContributionExtractor;

    public GithubContributionCalculator(
        PlatformContributionExtractor platformContributionExtractor
    ) {
        this.platformContributionExtractor = platformContributionExtractor;
    }

    @Override
    public Contribution calculate(String username) {
        return new Contribution(
            calculateStars(username),
            calculateCommits(username),
            calculatePRs(username),
            calculateIssues(username),
            calculateRepos(username)
        );
    }

    private int calculateStars(String username) {
        List<StarsDto> stars = platformContributionExtractor
            .extractStars(username);

        return stars.stream()
            .mapToInt(StarsDto::getStars)
            .sum();
    }

    private int calculateCommits(String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/commits?q=committer:%s", username);

        return count.getCount();
    }

    private int calculatePRs(String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:pr", username);

        return count.getCount();
    }

    private int calculateIssues(String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/issues?q=author:%s type:issue", username);

        return count.getCount();
    }

    private int calculateRepos(String username) {
        CountDto count = platformContributionExtractor
            .extractCount("/repositories?q=user:%s is:public", username);

        return count.getCount();
    }
}
