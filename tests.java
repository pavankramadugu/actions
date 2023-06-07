import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JdbcClientTest {

    @Mock
    private Vertx vertx;

    @Mock
    private JsonObject config;

    private JdbcClient jdbcClient;

    @BeforeEach
    public void setup() {
        // Instantiate the object under test
        jdbcClient = new JdbcClient();
    }

    @Test
    public void shouldReturnHikariJdbcClient() {
        // Given
        when(config.getString("CONNECTION_POOL")).thenReturn("HIKARI");
        when(config.getString("dataSourceName")).thenReturn("myDataSource");
        when(config.getJsonObject("connectionParams")).thenReturn(new JsonObject());

        // When
        JdbcClient result = jdbcClient.getJdbcClient(vertx, config);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof HikariJdbcClient);  // Assuming HikariJdbcClient is a subclass of JdbcClient
    }

    @Test
    public void shouldReturnC3POJdbcClient() {
        // Given
        when(config.getString("CONNECTION_POOL")).thenReturn(null);
        when(config.getString("dataSourceName")).thenReturn("myDataSource");
        when(config.getJsonObject("connectionParams")).thenReturn(new JsonObject());

        // When
        JdbcClient result = jdbcClient.getJdbcClient(vertx, config);

        // Then
        assertNotNull(result);
        assertTrue(result instanceof C3POJdbcClient);  // Assuming C3POJdbcClient is a subclass of JdbcClient
    }

    @Test
    public void shouldThrowExceptionWhenDataSourceNameMissing() {
        // Given
        when(config.getString("dataSourceName")).thenReturn(null);

        // When & Then
        assertThrows(InvalidJdbcConfigException.class, () -> jdbcClient.getJdbcClient(vertx, config));
    }

    @Test
    public void shouldThrowExceptionWhenConnectionParamsMissing() {
        // Given
        when(config.getJsonObject("connectionParams")).thenReturn(null);

        // When & Then
        assertThrows(InvalidJdbcConfigException.class, () -> jdbcClient.getJdbcClient(vertx, config));
    }
}
