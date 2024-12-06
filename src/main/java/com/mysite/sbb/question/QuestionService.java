package com.mysite.sbb.question;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mysite.sbb.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @SuppressWarnings("unused")
    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Question> getList(int page, String kw, String startDate, String endDate) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        //메서드에서 startDate와 endDate를 LocalDateTime으로 변환 후 추가 필터링
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).atStartOfDay();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createDate"), start));
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createDate"), end));
        }
        return this.questionRepository.findAll(spec, pageable);
    }
    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        }else{
            throw new DataNotFoundException("question not found");
        }
    }
    public void create (String subject, String content, SiteUser user,  Category category) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setCategory(category);
        this.questionRepository.save(q);
    }
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public List<Question> getCurrentListByUser(String username, int num) {
        Pageable pageable = PageRequest.of(0, num);
        return questionRepository.findCurrentQuestion(username, pageable);
    }
    // 조회수 증가 메서드
    @Transactional
    public void incrementViewCount(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() ->
                new IllegalArgumentException("해당 질문이 없습니다. ID: " + questionId));
        question.setViewCount(question.getViewCount() + 1);
    }
    // 조회수 증가 및 인기 게시글 갱신
    public void increaseViewCountAndUpdatePopular(Integer questionId) {
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        if (optionalQuestion.isPresent()) {
            Question currentQuestion = optionalQuestion.get();
            currentQuestion.setViewCount(currentQuestion.getViewCount() + 1); // 조회수 증가
            questionRepository.save(currentQuestion); // 저장

            // 인기 게시글 갱신
            updatePopularQuestion();
        } else {
            throw new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다: " + questionId);
        }
    }

    // 인기 게시글 갱신
    private void updatePopularQuestion() {
        // 가장 조회수가 높은 게시글 찾기
        List<Question> allQuestions = questionRepository.findAll(Sort.by(Sort.Direction.DESC, "viewCount"));
        if (!allQuestions.isEmpty()) {
            Question topQuestion = allQuestions.get(0); // 조회수 1위 게시글
            // 기존 인기 게시글 상태 초기화
            questionRepository.findAll().forEach(question -> {
                if (question.isPopular() && !question.equals(topQuestion)) {
                    question.setPopular(false); // 기존 인기 게시글에서 제거
                    questionRepository.save(question);
                }
            });
            // 새 인기 게시글로 설정
            if (!topQuestion.isPopular()) {
                topQuestion.setPopular(true);
                questionRepository.save(topQuestion);
            }
        }
    }

    public List<Question> getRecentQuestions(int limit) {
        return this.questionRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createDate"))).getContent();
    }

}