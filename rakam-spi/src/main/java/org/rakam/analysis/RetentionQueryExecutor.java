/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rakam.analysis;

import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.rakam.report.QueryExecution;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.rakam.util.ValidationUtil.checkCollection;

/**
 * Created by buremba <Burak Emre Kabakcı> on 27/08/15 06:41.
 */
public interface RetentionQueryExecutor {
    QueryExecution query(String project,
                         Optional<RetentionAction> firstAction,
                         Optional<RetentionAction> returningAction,
                         DateUnit dateUnit,
                         Optional<String> dimension,
                         LocalDate startDate,
                         LocalDate endDate);

    @AutoValue
    abstract class RetentionAction {
        private static SqlParser parser = new SqlParser();

        @JsonProperty
        public abstract String collection();
        @JsonProperty
        public abstract Optional<Expression> filter();

        @JsonCreator
        public static RetentionAction create(@JsonProperty("collection") String collection,
                                        @JsonProperty("filterExpression") Optional<String> filterExpression) {
            checkCollection(collection);
            return new AutoValue_RetentionQueryExecutor_RetentionAction(collection,
                    filterExpression.map(RetentionAction::parseExpression));
        }

        private static synchronized Expression parseExpression(String filterExpression) {
            return parser.createExpression(filterExpression);
        }
    }

    enum DateUnit {
        DAY(ChronoUnit.DAYS), WEEK(ChronoUnit.WEEKS), MONTH(ChronoUnit.MONTHS);

        private final ChronoUnit temporalUnit;

        DateUnit(ChronoUnit temporalUnit) {
            this.temporalUnit = temporalUnit;
        }

        @JsonCreator
        public static DateUnit get(String name) {
            return valueOf(name.toUpperCase());
        }

        public ChronoUnit getTemporalUnit() {
            return temporalUnit;
        }
    }
}