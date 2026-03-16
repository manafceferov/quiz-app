package com.quiz.repository;

import com.quiz.dto.profile.ProfileProjectionEdit;
import com.quiz.entity.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByEmail(String email);

    @Query("""
                select
                    p.id as id,
                    p.firstName as firstName,
                    p.lastName as lastName,
                    p.fatherName as fatherName,
                    p.email as email,
                    p.password as password,
                    p.phoneNumber as phoneNumber,
                    p.birthDate as birthDate,
                    p.gender as gender,
                    a.id as attachmentId,
                    a.fileUrl as attachmentUrl
                from Participant p
                left join p.attachment a
                where p.id = :id
            """)
    Optional<ProfileProjectionEdit> findProfileProjectionById(@Param("id") Long id);


    @Modifying
    @Query("UPDATE Participant p SET p.status = :status WHERE p.id = :id")
    void changeStatus(@Param("id") Long id, @Param("status") Boolean status);

    @Query("SELECT p FROM Participant p WHERE " + "LOWER(CONCAT(p.firstName, ' ', p.lastName, ' ', p.fatherName)) " +
            "LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Participant> searchByFullName(@Param("name") String name, Pageable pageable);

}
