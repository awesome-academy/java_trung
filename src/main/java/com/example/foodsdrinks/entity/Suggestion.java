package com.example.foodsdrinks.entity;

import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.entity.enums.SuggestionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
	private User user;

	@Column(nullable = false, length = 200)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private Classify classify;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	@Builder.Default
	private SuggestionStatus status = SuggestionStatus.PENDING;

	@Column(name = "admin_note", columnDefinition = "TEXT")
	private String adminNote;
}
