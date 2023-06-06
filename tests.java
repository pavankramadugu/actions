import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DatabaseClientFactoryTest {
    private Vertx vertx;
    private DatabaseClientFactory factory;

    @BeforeEach
    void setup() {
        vertx = Mockito.mock(Vertx.class);
        factory = DatabaseClientFactory.getInstance();
    }

    @Test
    void testGetClientWithExistingClient() {
        JsonObject config = new JsonObject().put("dataSourceName", "existingDataSource");

        // Setup an existing client in the map
        DatabaseClient existingClient = Mockito.mock(DatabaseClient.class);
        factory.getClient(vertx, config);  // this line puts the client into the map

        DatabaseClient client = factory.getClient(vertx, config);

        assertThat(client).isNotNull();
        assertThat(client).isSameAs(existingClient);
    }

    @Test
    void testGetClientWithNewClient() {
        JsonObject config = new JsonObject().put("dataSourceName", "newDataSource");

        DatabaseClient client = factory.getClient(vertx, config);

        assertThat(client).isNotNull();
        assertThat(client).isInstanceOf(Vertx3JdbcClient.class);
    }

    @Test
    void testGetClientWithoutDataSourceName() {
        JsonObject config = new JsonObject();

        assertThatThrownBy(() -> factory.getClient(vertx, config))
                .isInstanceOf(InvalidJdbcConfigException.class)
                .hasMessage("Data Source name is not Present");
    }
}
