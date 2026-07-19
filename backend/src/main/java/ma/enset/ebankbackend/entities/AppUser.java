package ma.enset.ebankbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    // Exemple: "USER" ou "USER,ADMIN"
    private String roles;
}
