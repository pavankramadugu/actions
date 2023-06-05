@Test
void testSelectWithoutParams(VertxTestContext testContext) {
        // Mock connection and result set
        SQLConnection sqlConnection = Mockito.mock(SQLConnection.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        // Prepare the expected SQL query
        String expectedSql = "SELECT * FROM test";

        // Mock a successful connection
        when(jdbcClient.getConnection(any())).thenAnswer(invocation -> {
        Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
        handler.handle(Future.succeededFuture(sqlConnection));
        return null;
        });

        // Mock a successful query
        when(sqlConnection.query(eq(expectedSql), any())).thenAnswer(invocation -> {
        Handler<AsyncResult<ResultSet>> handler = invocation.getArgument(1);
        handler.handle(Future.succeededFuture(resultSet));
        return null;
        });

        // Call the select method without params
        dbClient.select(expectedSql)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {
        assertSame(resultSet, result);
        testContext.completeNow();
        })));
        }



@Test
void testSelectWithParams(VertxTestContext testContext) {
        // Mock connection and result set
        SQLConnection sqlConnection = Mockito.mock(SQLConnection.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        // Prepare the expected SQL query and parameters
        String expectedSql = "SELECT * FROM test WHERE id = ?";
        JsonArray expectedParams = new JsonArray().add("param1");

        // Mock a successful connection
        when(jdbcClient.getConnection(any())).thenAnswer(invocation -> {
        Handler<AsyncResult<SQLConnection>> handler = invocation.getArgument(0);
        handler.handle(Future.succeededFuture(sqlConnection));
        return null;
        });

        // Mock a successful query
        when(sqlConnection.queryWithParams(
        eq(expectedSql),
        argThat(params -> params.equals(expectedParams)), // Expect the JsonArray with "param1"
        any())).thenAnswer(invocation -> {
        Handler<AsyncResult<ResultSet>> handler = invocation.getArgument(2);
        handler.handle(Future.succeededFuture(resultSet));
        return null;
        });

        // Call the select method with params
        dbClient.select(expectedSql, expectedParams)
        .onComplete(testContext.succeeding(result -> testContext.verify(() -> {
        assertSame(resultSet, result);
        testContext.completeNow();
        })));
        }
