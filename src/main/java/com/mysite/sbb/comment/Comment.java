package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(columnDefinition = "TEXT") private String content;

    private LocalDateTime createDate;
    @ManyToOne
    private Question question;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private SiteUser author;
}