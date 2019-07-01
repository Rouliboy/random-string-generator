package com.total.unique.generator.service;

import com.total.unique.generator.entity.UniqueID;
import com.total.unique.generator.repository.UniqueIDRepository;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AsyncUniqueIdUpdater {

  private final JdbcTemplate jdbcTemplate;

  private final UniqueIDRepository uniqueIDRepository;

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Future<Long> update(final int count) {

    final List<String> uniqueIds = uniqueIDRepository
        .findUniquesIdsNotWithStatus(UniqueID.Status.PROCESSED, PageRequest.of(0, count));

    log.info("Unique ids = {}", uniqueIds.size());

    final String query = "update UNIQUE_ID set status = ? where unique_id = ?";
    final int[] result = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(final PreparedStatement preparedStatement, final int i)
          throws SQLException {
        preparedStatement.setString(1, UniqueID.Status.PROCESSED.name());
        preparedStatement.setString(2, uniqueIds.get(i));
      }

      @Override
      public int getBatchSize() {

        return uniqueIds.size();
      }
    });

    log.info("finished processing Unique ids = {}", uniqueIds.size());
    return new AsyncResult(Long.valueOf(result.length));
  }
}
