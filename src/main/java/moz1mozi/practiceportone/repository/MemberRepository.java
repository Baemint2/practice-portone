package moz1mozi.practiceportone.repository;

import moz1mozi.practiceportone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
