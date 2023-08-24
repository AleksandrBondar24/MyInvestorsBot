package bondar.MyInvestorsBot.repository;

import bondar.MyInvestorsBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
