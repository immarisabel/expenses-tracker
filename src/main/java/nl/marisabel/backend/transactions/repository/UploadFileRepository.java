package nl.marisabel.backend.transactions.repository;

import nl.marisabel.backend.transactions.entity.UploadFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFileEntity, Long> {
}
