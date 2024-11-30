package com.mysite.sbb.question;

import java.security.Principal;
import java.util.List;

import com.mysite.sbb.Category;
import com.mysite.sbb.CategoryService;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.comment.CommentForm;
import com.mysite.sbb.comment.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final CategoryService categoryService;



    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        log.info("page:{}, kw:{}", page, kw);
        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }
    // 추가
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id,
                         @RequestParam(value = "answerPage", defaultValue = "0") int answerPage) {
        questionService.increaseViewCountAndUpdatePopular(id); // 조회수 증가 및 인기 게시글 갱신
        Question question = this.questionService.getQuestion(id);
        Page<Answer> answerPaging = this.answerService.getList(question, answerPage);
        List<Comment> commentList = this.commentService.getCommentList(question);
        /*questionService.incrementViewCount(id); */// 조회수 증가

        // AnswerForm과 CommentForm 추가
        model.addAttribute("question", question);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("commentList", commentList);
        model.addAttribute("answerForm", new AnswerForm()); // AnswerForm 객체 추가
        model.addAttribute("commentForm", new CommentForm()); // CommentForm 객체 추가

        return "question_detail";
    }
    /*@GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }*/

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm, Model model) {
        List<Category> categories = this.categoryService.getAllCategories();
        model.addAttribute("categories", categories); // 카테고리 목록 전달
        model.addAttribute("questionForm", new QuestionForm());
        // 디버깅용 출력
        System.out.println("카테고리 목록: " + categories);

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult  bindingResult,@RequestParam("categoryId") Integer categoryId, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category = this.categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
        return "redirect:/question/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    // 게시글 수정, 폼 호출
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}