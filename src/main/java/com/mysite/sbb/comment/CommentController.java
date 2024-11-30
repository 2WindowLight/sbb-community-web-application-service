package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/question/{id}")
    public String questionCommentCreate(Model model, @PathVariable("id") Integer id,
                                        @Valid CommentForm commentForm,
                                        BindingResult bindingResult, Principal principal) {
        // 질문 가져오기
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("commentList", this.commentService.getCommentList(question)); // 댓글 리스트 추가
            return "question_detail";
        }

        // 댓글 생성
        this.commentService.create(commentForm.getContent(), question, null, siteUser);

        // 리다이렉션
        return String.format("redirect:/question/detail/%d", id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/answer/{id}")
    public String answerCommentCreate(Model model, @PathVariable("id") Integer id,
                                      @Valid CommentForm commentForm,
                                      BindingResult bindingResult, Principal principal) {
        // 답변 및 관련 질문 가져오기
        Answer answer = this.answerService.getAnswer(id);
        Question question = answer.getQuestion();
        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("commentList", this.commentService.getCommentList(question)); // 댓글 리스트 추가
            return "question_detail";
        }

        // 댓글 생성
        this.commentService.create(commentForm.getContent(), question, answer, siteUser);

        // 리다이렉션
        return String.format("redirect:/question/detail/%d#answer_%d", question.getId(), id);
    }
}