package de.fxnn.geld.application.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.fxnn.geld.io.ikonli.CategoryIcon;
import org.junit.jupiter.api.Test;

class WorkspaceModelTest {

  @Test
  void assignCategoryIcon__existingCategory__appendsExpression() {
    // given
    var sut = new WorkspaceModel();
    sut.assignCategoryIcon(CategoryIcon.ANIMAL, "test1 test2");

    // when
    sut.assignCategoryIcon(CategoryIcon.ANIMAL, "test3");

    // then
    assertThat(sut.getCategoryMap().keySet(), contains(CategoryIcon.ANIMAL));
    assertEquals(
        sut.getCategoryMap().get(CategoryIcon.ANIMAL).getFilterExpression(), "test1 test2 test3");
  }
}
