package com.springstudy.backend.Config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class ForeignKeyModifier {

    private final DataSource dataSource;

    public ForeignKeyModifier(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void modifyForeignKey() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // 외래 키 삭제
            statement.executeUpdate("ALTER TABLE screening DROP FOREIGN KEY FK_screening_TO_movie_1");

            // 외래 키 다시 추가 (ON DELETE CASCADE)
            statement.executeUpdate("ALTER TABLE screening ADD CONSTRAINT FK_screening_TO_movie_1 FOREIGN KEY (movieid) REFERENCES movie(id) ON DELETE CASCADE");

            System.out.println("✅ Foreign key successfully modified (via JDBC).");

        } catch (Exception e) {
            System.err.println("❌ Failed to modify foreign key: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
