package com.shopwave.shopwave.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity                          // JPA: this class maps to a database table
@Table(name = "users")           // the table it maps to (created by the Flyway migration)
@Getter                          // Lombok: generates a getter for every field
@Setter                          // Lombok: generates a setter for every field
@NoArgsConstructor               // Lombok: generates the empty constructor that Hibernate needs
public class Users {

    @Id                          // this field is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // the database generates the value (BIGSERIAL auto-increment)
    @Setter(AccessLevel.NONE)    // no setter: your code should never set the id
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    // name: the column is user_name, but the Java field is userName
    // nullable = false: required (matches NOT NULL in the SQL)
    // unique = true: no two users share a username (matches UNIQUE)
    // length = 50: matches VARCHAR(50)
    private String userName;

    @Column(nullable = false, unique = true)
    // no name needed: the column is also called email
    private String email;

    @Column(name = "password_hash", nullable = false)
    // stores the BCrypt hash, never the plain password
    private String passwordHash;

    @Enumerated(EnumType.STRING) // store the enum as text ('USER'), not as a number (0)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
    // default for a new Users object, so new accounts are never admins

    @Column(nullable = false)
    private boolean enabled = true;
    // lets you disable an account without deleting it; defaults to active

    @CreationTimestamp           // Hibernate fills this with the current time when the row is inserted
    @Column(name = "created_at", nullable = false, updatable = false)
    // updatable = false: never changes after the first insert
    @Setter(AccessLevel.NONE)    // no setter: set by Hibernate, not by your code
    private Instant createdAt;
}