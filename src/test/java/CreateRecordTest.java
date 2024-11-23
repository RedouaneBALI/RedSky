import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.redouanebali.library.BlueskyClient;
import io.github.redouanebali.library.dto.CreateRecordResponse;
import io.github.redouanebali.library.dto.DeleteRecordResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CreateRecordTest {

  private static final BlueskyClient BS_CLIENT = new BlueskyClient();

  @BeforeAll
  public static void init() throws IOException {
    String username = System.getenv("BLUESKY_USERNAME");
    String password = System.getenv("BLUESKY_PASSWORD");
    BS_CLIENT.login(username, password);
  }

  @Test
  @Disabled
  public void createRecordTest() throws IOException {
    CreateRecordResponse createResponse = BS_CLIENT.createRecord("Et hop, je viens d'apprendre à pouvoir supprimer mes posts ! \uD83D\uDE0F ✅");
    assertNotNull(createResponse.getUri());
  }

  @Test
  public void createAndDeleteRecordTest() throws IOException {
    CreateRecordResponse createResponse = BS_CLIENT.createRecord("A supprimer...");
    assertNotNull(createResponse.getUri());
    DeleteRecordResponse deleteResponse = BS_CLIENT.deleteRecord(createResponse.getRkey());
    assertNotNull(deleteResponse.getCommit().getCid());
    assertNotNull(deleteResponse.getCommit().getRev());
    System.out.println(deleteResponse);
  }

}
