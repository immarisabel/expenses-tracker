package nl.marisabel.backend.error;

public class ResourceNotFoundException extends RuntimeException {
 public ResourceNotFoundException(String message) {
  super(message);
 }
}
