package conference.clerker.domain.schedule.repository;

import conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO;
import conference.clerker.domain.schedule.schema.ScheduleTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ScheduleTimeRepository extends JpaRepository<ScheduleTime, Long> {
    @Query("SELECT new conference.clerker.domain.schedule.dto.response.ScheduleTimeWithMemberInfoDTO(" +
            "st, m.username, m.email, o.type) " +
            "FROM ScheduleTime st " +
            "JOIN Organization o ON o.member.id = st.memberId " +
            "JOIN Member m ON m.id = o.member.id " +
            "WHERE st.schedule.id = :scheduleId " +
            "AND o.project.id = :projectId")
    List<ScheduleTimeWithMemberInfoDTO> findScheduleTimeWithMemberInfoByScheduleIdAndProjectId(
            @Param("scheduleId") Long scheduleId,
            @Param("projectId") Long projectId);


}