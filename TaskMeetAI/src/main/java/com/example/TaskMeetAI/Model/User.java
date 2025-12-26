package com.example.TaskMeetAI.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;          // Mongo ObjectId as String

    private String fullName;

    private String email;       // enforce uniqueness via repo check or index

    private String password;

    private String role;        // MANAGER or EMPLOYEE



    private Date createAt = new Date();
}