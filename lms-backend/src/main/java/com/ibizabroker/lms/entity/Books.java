package com.ibizabroker.lms.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "books")
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    private String bookName;
    private String bookAuthor;
    private String bookGenre;
    private Integer noOfCopies;

    public void borrowBook() {
        this.noOfCopies--;
    }

    public void returnBook() {
        this.noOfCopies++;
    }
}
