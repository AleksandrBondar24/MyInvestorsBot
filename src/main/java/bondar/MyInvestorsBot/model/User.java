package bondar.MyInvestorsBot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @Column(name = "idChat")
    private Long idChat;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "userName")
    private String userName;
    @Column(name = "registerTime")
    private LocalDateTime registerTime;
}
