package nl.marisabel.transactions.interfaces;

import nl.marisabel.transactions.classes.UploadFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFileEntity, Long> {
}
