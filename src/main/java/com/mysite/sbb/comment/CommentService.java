package com.mysite.sbb.comment;


import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    public Comment create(String content, Question question, Answer answer, SiteUser siteUser) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setQuestion(question);
        comment.setAnswer(answer);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(comment);
        return comment;

    }
    public List<Comment> getCommentList(Question question) {
        return this.commentRepository.findByQuestion(question);
    }
    public List<Comment> getCurrentListByUser(String username, int num) {
        Pageable pageable = PageRequest.of(0, num);
        return commentRepository.findCurrentComment(username, pageable);
    }

}
