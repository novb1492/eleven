package com.www.eleven.Member.Repo;

import com.www.eleven.Member.Model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepo extends JpaRepository<MemberEntity,Long> {

    @Query("select m from MemberEntity m where m.commonColumn.state=:state and m.userId=:email")
    public Optional<MemberEntity> findByState(@Param("state") int state, @Param("email") String email);

}
