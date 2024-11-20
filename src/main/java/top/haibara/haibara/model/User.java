package top.haibara.haibara.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "nickname")
    private String nickname;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role = "user";
}
