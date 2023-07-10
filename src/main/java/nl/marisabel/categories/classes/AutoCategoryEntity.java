package nl.marisabel.categories.classes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AutoCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    @ElementCollection
    @CollectionTable(name = "query", joinColumns = @JoinColumn(name = "auto_category_entity_id"))
    @Column(name = "query")
    private List<String> queries;

}
