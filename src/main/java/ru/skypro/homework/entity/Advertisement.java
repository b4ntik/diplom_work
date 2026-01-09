package ru.skypro.homework.entity;

import javax.persistence.*;

@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private double price;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
