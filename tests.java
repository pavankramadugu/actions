package com.example;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.unit.junit.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
public class DBClientTest {

    private DBClient dbClient;
    private JDBCClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient = Mockito.mock(JDBCClient.class);
        dbClient = new DBClient(jdbcClient);
    }

    @Test
    void testCall(VertxTestContext testContext) {
        // Mock connection and result set
        SQLConnection sqlConnection = Mockito.mock(SQLConnection.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        // Capture the argument to getConnection
        ArgumentCaptor<Handler<AsyncResult<SQLConnection>>> connectionCaptor = ArgumentCaptor.forClass(Handler.class);

        // Mock a successful connection
        doNothing().when(jdbcClient).getConnection(connectionCaptor.capture());

        // Capture the arguments to callWithParams
        ArgumentCaptor<Handler<AsyncResult<ResultSet>>> resultCaptor = ArgumentCaptor.forClass(Handler.class);

        // Mock a successful query
        doNothing().when(sqlConnection).callWithParams(eq("SELECT * FROM test WHERE id = ?"), eq(new JsonArray().add("param1").add("param2")), eq(new JsonArray().add("output1").add("output2")), resultCaptor.capture());

        // Params for the call function
        JsonArray params = new JsonArray().add("param1").add("param2");
        JsonArray outputs = new JsonArray().add("output1").add("output2");

        dbClient.call("SELECT * FROM test WHERE id = ?", params, outputs)
                .onComplete(testContext.succeeding(result -> testContext.verify(() -> {
                    assertSame(resultSet, result);
                    testContext.completeNow();
                })));

        // Invoke the handlers with mock results
        connectionCaptor.getValue().handle(Future.succeededFuture(sqlConnection));
        resultCaptor.getValue().handle(Future.succeededFuture(resultSet));

        // Test the failed connection branch
        dbClient.call("SELECT * FROM test WHERE id = ?", params, outputs)
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals("Failed to get connection", throwable.getMessage());
                    testContext.completeNow();
                })));

        // Invoke the handler with a failure
        connectionCaptor.getValue().handle(Future.failedFuture("Failed to get connection"));

        // Test the failed query branch
        dbClient.call("SELECT * FROM test WHERE id = ?", params, outputs)
                .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
                    assertEquals("Failed to execute query", throwable.getMessage());
                    testContext.completeNow();
                })));

        // Invoke the handler with a failure
        resultCaptor.getValue().handle(Future.failedFuture("Failed to execute query"));
    }
}
