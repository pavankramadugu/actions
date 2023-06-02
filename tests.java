import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class JdbcServiceTest {

    @Test
    void testExecuteInTransaction_Success() {
        JDBCClient client = mock(JDBCClient.class);
        SQLConnection connection = mock(SQLConnection.class);
        JdbcService jdbcService = new JdbcService(client);

        when(client.getConnection(any())).thenAnswer(invocation -> {
            Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
            handler.handle(Future.succeededFuture(connection));
            return null;
        });

        when(connection.updateWithParams(any(), any(), any())).thenAnswer(invocation -> {
            Handler<AsyncResult<UpdateResult>> handler = invocation.getArgument(2);
            handler.handle(Future.succeededFuture(new UpdateResult()));
            return connection;
        });

        jdbcService.executeInTransaction(Arrays.asList("sql1", "sql2"), Arrays.asList(new JsonArray(), new JsonArray())).setHandler(ar -> {
            assertTrue(ar.succeeded());
        });

        verify(client, times(1)).getConnection(any());
        verify(connection, times(2)).updateWithParams(any(), any(), any());
        verify(connection, times(1)).commit(any());
        verify(connection, times(1)).close();
    }

    @Test
    void testExecuteInTransaction_GetConnectionFailure() {
        JDBCClient client = mock(JDBCClient.class);
        JdbcService jdbcService = new JdbcService(client);

        when(client.getConnection(any())).thenAnswer(invocation -> {
            Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
            handler.handle(Future.failedFuture("Connection failure"));
            return null;
        });

        jdbcService.executeInTransaction(Arrays.asList("sql1", "sql2"), Arrays.asList(new JsonArray(), new JsonArray())).setHandler(ar -> {
            assertTrue(ar.failed());
            assertEquals("Connection failure", ar.cause().getMessage());
        });

        verify(client, times(1)).getConnection(any());
    }

    @Test
    void testExecuteInTransaction_SetAutoCommitFailure() {
        JDBCClient client = mock(JDBCClient.class);
        SQLConnection connection = mock(SQLConnection.class);
        JdbcService jdbcService = new JdbcService(client);

        when(client.getConnection(any())).thenAnswer(invocation -> {
            Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
            handler.handle(Future.succeededFuture(connection));
            return null;
        });

        doAnswer(invocation -> {
            Handler<AsyncResult<Void>> handler = invocation.getArgument(0);
            handler.handle(Future.failedFuture("Auto commit failure"));
            return null;
        }).when(connection).setAutoCommit(eq(false), any());

        jdbcService.executeInTransaction(Arrays.asList("sql1", "sql2"), Arrays.asList(new JsonArray(), new JsonArray())).setHandler(ar -> {
            assertTrue(ar.failed());
            assertEquals("Auto commit failure", ar.cause().getMessage());
        });

        verify(client, times(1)).getConnection(any());
        verify(connection, times(1)).setAutoCommit(eq(false), any());
    }

    @Test
    void testExecuteInTransaction_SQLOperationFailure() {
        JDBCClient client = mock(JDBCClient.class);
        SQLConnection connection = mock(SQLConnection.class);
        JdbcService jdbcService = new JdbcService(client);

        when(client.getConnection(any())).thenAnswer(invocation -> {
            Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
            handler.handle(Future.succeededFuture(connection));
            return null;
        });

        when(connection.updateWithParams(any(), any(), any())).thenAnswer(invocation -> {
            Handler<AsyncResult<UpdateResult>> handler = invocation.getArgument(2);
            handler.handle(Future.failedFuture("SQL operation failure"));
            return connection;
        });

        jdbcService.executeInTransaction(Arrays.asList("sql1", "sql2"), Arrays.asList(new JsonArray(), new JsonArray())).setHandler(ar -> {
            assertTrue(ar.failed());
            assertEquals("SQL operation failure", ar.cause().getMessage());
        });

        verify(client, times(1)).getConnection(any());
        verify(connection, times(1)).updateWithParams(any(), any(), any());
        verify(connection, times(1)).rollback(any());
        verify(connection, times(1)).close();
    }

    @Test
    void testExecuteInTransaction_CommitFailure() {
        JDBCClient client = mock(JDBCClient.class);
        SQLConnection connection = mock(SQLConnection.class);
        JdbcService jdbcService = new JdbcService(client);

        when(client.getConnection(any())).thenAnswer(invocation -> {
            Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
            handler.handle(Future.succeededFuture(connection));
            return null;
        });

        when(connection.updateWithParams(any(), any(), any())).thenAnswer(invocation -> {
            Handler<AsyncResult<UpdateResult>> handler = invocation.getArgument(2);
            handler.handle(Future.succeededFuture(new UpdateResult()));
            return connection;
        });

        doAnswer(invocation -> {
            Handler<AsyncResult<Void>> handler = invocation.getArgument(0);
            handler.handle(Future.failedFuture("Commit failure"));
            return null;
        }).when(connection).commit(any());

        jdbcService.executeInTransaction(Arrays.asList("sql1", "sql2"), Arrays.asList(new JsonArray(), new JsonArray())).setHandler(ar -> {
            assertTrue(ar.failed());
            assertEquals("Commit failure", ar.cause().getMessage());
        });

        verify(client, times(1)).getConnection(any());
        verify(connection, times(2)).updateWithParams(any(), any(), any());
        verify(connection, times(1)).commit(any());
        verify(connection, times(1)).close();
    }
}
