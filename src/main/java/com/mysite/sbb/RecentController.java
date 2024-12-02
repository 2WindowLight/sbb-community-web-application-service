package com.mysite.sbb;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.answer.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
@RequiredArgsConstructor
@Controller
public class RecentController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/recent-posts")
    public String showRecentPosts(Model model) {
        // 최신 게시글 5개
        List<Question> questions = this.questionService.getRecentQuestions(5);

        // 최신 답변 5개
        List<Answer> answers = this.answerService.getRecentAnswers(5);

        model.addAttribute("questions", questions);
        model.addAttribute("answers", answers);

        return "recent_posts";
    }
}
