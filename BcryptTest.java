import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "admin123";
        String hash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Romltoaj9aVO90xGmKYyJWGy";

        System.out.println("Testing password: " + password);
        System.out.println("Against hash: " + hash);
        System.out.println("Matches: " + encoder.matches(password, hash));

        // Generate a new hash
        String newHash = encoder.encode(password);
        System.out.println("\nNew hash for 'admin123': " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}
