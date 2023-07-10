package nl.marisabel.utils.error;

public class ResourceNotFoundException extends RuntimeException {
 public ResourceNotFoundException(String message) {
  super(message);
 }
}
