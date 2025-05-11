package os.automation_check_file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import os.automation_check_file.entity.BaseFile;

import java.util.UUID;

public interface BaseFileRepository extends JpaRepository<BaseFile, UUID> {
}
