package com.urosdragojevic.realbookstore.repository;

import com.urosdragojevic.realbookstore.domain.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CommentRepository.class);


    private DataSource dataSource;

    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(Comment comment) {
        String query = "insert into comments(bookId, userId, comment) values (" + comment.getBookId() + ", " + comment.getUserId() + ", '" + comment.getComment() + "')";

        // When a user submits a comment, the comment is inserted into the database without any validation or sanitization.
        // This allows for SQL injection and XSS attacks.
        // Test the application by submitting the following comments on http://localhost:8080/books/{id}:


        // SQL injection
        // We can see that the query is constructed by concatenating strings, which is a bad practice.
        // komentar'); insert into persons(firstName, lastName, email) values ('Julijana', 'Jevtic', 'julijana@gmail.com'); --

        // SQL injection & XSS
        // Xss attack is happening when we search for a user which we're adding to the database.
        // Picture source doesn't exist, but onerror is triggered and we get an alert with the cookie.
        // komentar'); insert into persons(firstName, lastName, email) values ('Julijana', 'Jevtic', '<img src="neki.jpg" onerror="alert(document.cookie);">'); --


        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        ) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Comment> getAll(int bookId) {
        List<Comment> commentList = new ArrayList<>();
        String query = "SELECT bookId, userId, comment FROM comments WHERE bookId = " + bookId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                commentList.add(new Comment(rs.getInt(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentList;
    }
}
