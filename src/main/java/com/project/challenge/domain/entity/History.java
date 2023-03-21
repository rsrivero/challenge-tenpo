package com.project.challenge.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.hibernate.annotations.Type;
import java.sql.Timestamp;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;

@Table
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp timestamp;

    private String method;

    private String uri;

    private int statusCode;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private JsonNode response;
}
