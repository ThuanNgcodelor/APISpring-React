//package com.example.apijava.models;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.sql.Date;
//
//
//@Data
//@Entity
//@Table(name = "episode")
//@AllArgsConstructor
//@NoArgsConstructor
//public class Episode {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "anime_id",nullable = false)
//    private Anime anime;
//    private String fileName;
//    private Integer quantity;
//    @Temporal(TemporalType.DATE)
//    private Date dateTime;
//    private String linkVideo;
//}
