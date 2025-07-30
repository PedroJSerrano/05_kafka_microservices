package pjserrano.authmicroservices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table("roles")
public class Role implements Persistable<Integer> {
    @Id
    private Integer id;

    @Column("name")
    private String name; // Ej: "ADMIN", "USER"

    @Transient
    private boolean isNew;

    @Override
    public Integer getId() { return this.id; }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
