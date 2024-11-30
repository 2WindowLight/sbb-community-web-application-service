package com.mysite.sbb.question;

import com.mysite.sbb.Category;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {
    @Id // id를 pk로 쓰겠다
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    private LocalDateTime modifyDate;

    @ManyToOne
    private Category category; // 게시글 카테고리


    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int viewCount; // 조회수

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular = false;

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }
}
