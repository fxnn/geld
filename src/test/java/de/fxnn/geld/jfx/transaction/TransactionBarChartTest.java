package de.fxnn.geld.jfx.transaction;

import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import lombok.Value;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.DebugUtils;

@ExtendWith(ApplicationExtension.class)
class TransactionBarChartTest {

  private static final double MAX_DELTA = 0.001d;

  WorkspaceModel model;
  TransactionBarChart sut;

  @Start
  private void start(Stage stage) {
    model = new WorkspaceModel();
    sut = new TransactionBarChart(model);
    stage.setScene(new Scene(sut));
    stage.show();
  }

  @Test
  void add_singleDataPoint(FxRobot robot, TestInfo testInfo) {
    with(robot, testInfo)
        .given()
        .transaction(LocalDate.of(2021, 1, 1), BigDecimal.valueOf(1.1d))
        .then()
        .sut(hasData("2021-01", "1.1"));
  }

  @Test
  void addAndRemove_oneOfTwoPoints(FxRobot robot, TestInfo testInfo) {
    with(robot, testInfo)
        .given()
        .transaction(LocalDate.of(2021, 1, 1), BigDecimal.valueOf(1.1d))
        .transaction(LocalDate.of(2021, 2, 2), BigDecimal.valueOf(2.2d))
        .when()
        .filter(transactionModel -> transactionModel.getDate().getMonthValue() == 1)
        .awaitSutAnimationEnd()
        .then()
        .sut(hasData("2021-01", "1.1"))
        .sut(hasData("2021-02", "0"));
  }

  private FixtureBuilder with(FxRobot robot, TestInfo testInfo) {
    return new FixtureBuilder(robot, testInfo);
  }

  @Value
  private class AssertionBuilder {
    TestInfo testInfo;

    public AssertionBuilder sut(Matcher<TransactionBarChart> matcher) {
      FxAssert.verifyThat(sut, matcher, saveScreenshot());
      return this;
    }

    private Function<StringBuilder, StringBuilder> saveScreenshot() {
      return DebugUtils.saveScreenshot(
          () ->
              createDirectories(Paths.get("target", "screenshots")).resolve(createImageFileName()),
          "  ");
    }

    private Path createDirectories(Path directory) {
      try {
        return Files.createDirectories(directory);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private Path createImageFileName() {
      var testName =
          testInfo.getTestClass().map(Class::getSimpleName).orElse("")
              + " - "
              + testInfo.getDisplayName().replaceAll("[^A-Za-z0-9]", "_");
      return DebugUtils.defaultImagePath(testName).get().getFileName();
    }
  }

  @Value
  private class FixtureBuilder {
    FxRobot robot;
    TestInfo testInfo;

    public FixtureBuilder given() {
      return this;
    }

    public FixtureBuilder when() {
      return this;
    }

    public AssertionBuilder then() {
      return new AssertionBuilder(testInfo);
    }

    public FixtureBuilder filter(Predicate<? super TransactionModel> predicate) {
      robot.interact(() -> model.getFilteredTransactionList().setPredicate(predicate));
      return this;
    }

    public FixtureBuilder transaction(LocalDate date, BigDecimal amount) {
      robot.interact(
          () -> {
            var tx = new TransactionModel();
            tx.setDate(date);
            tx.setAmount(amount);
            model.getTransactionList().add(tx);
          });
      return this;
    }

    public FixtureBuilder awaitSutAnimationEnd() {
      robot.interrupt();
      return this;
    }
  }

  private Matcher<TransactionBarChart> hasDataThat(Matcher<? super Collection<Data>> matcher) {
    return new TypeSafeDiagnosingMatcher<>() {
      @Override
      protected boolean matchesSafely(TransactionBarChart item, Description mismatchDescription) {
        boolean first = true;

        for (Series<String, Number> series : item.getData()) {
          if (matcher.matches(series.getData())) {
            return true;
          }
          if (!first) {
            mismatchDescription.appendText(" and ");
          }
          matcher.describeMismatch(series.getData(), mismatchDescription);
          first = false;
        }

        return false;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("has data that ").appendDescriptionOf(matcher);
      }
    };
  }

  private Matcher<TransactionBarChart> hasData(String expectedCategory, String expectedValue) {
    return new TypeSafeDiagnosingMatcher<>() {
      private final BigDecimal expectedBigDecimal = new BigDecimal(expectedValue);

      @Override
      protected boolean matchesSafely(
          TransactionBarChart transactionBarChart, Description mismatchDescription) {
        mismatchDescription.appendText("contained ");

        for (Series<String, Number> series : transactionBarChart.getData()) {
          for (Data<String, Number> data : series.getData()) {
            var actualValue = data.getYValue();
            mismatchDescription.appendText(formatDataPoint(data.getXValue(), actualValue));
            if (!expectedCategory.equals(data.getXValue())) {
              continue;
            }
            if (actualValue instanceof BigDecimal && isNearlyExpected((BigDecimal) actualValue)) {
              return true;
            }
            if (actualValue instanceof Double && isNearlyExpected((Double) actualValue)) {
              return true;
            }
          }
        }

        return false;
      }

      @Override
      public void describeTo(Description description) {
        description
            .appendText("has data point ")
            .appendText(formatDataPoint(expectedCategory, expectedBigDecimal));
      }

      private boolean isNearlyExpected(BigDecimal actualValue) {
        return expectedBigDecimal
                .subtract(actualValue)
                .abs()
                .compareTo(BigDecimal.valueOf(MAX_DELTA))
            < 0;
      }

      private boolean isNearlyExpected(Double actualValue) {
        return Math.abs(expectedBigDecimal.doubleValue() - actualValue) < MAX_DELTA;
      }

      private String formatDataPoint(String category, Object value) {
        var result = new StringBuilder();
        result.append("<").append(category).append(", ");
        if (value == null) {
          result.append("null");
        } else {
          result.append(value.getClass().getSimpleName()).append(":").append(value);
        }
        result.append(">");
        return result.toString();
      }
    };
  }
}
