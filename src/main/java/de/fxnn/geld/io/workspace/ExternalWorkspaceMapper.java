package de.fxnn.geld.io.workspace;

import static java.util.stream.Collectors.toList;

import de.fxnn.geld.application.model.WorkspaceModel;
import de.fxnn.geld.category.model.CategoryModel;
import de.fxnn.geld.io.ikonli.CategoryIcon;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {FXCollections.class, Collectors.class, CategoryIcon.class})
public interface ExternalWorkspaceMapper {

  ExternalWorkspace toExternal(WorkspaceModel internal);

  default WorkspaceModel toInternal(ExternalWorkspace external) {
    var result = toInternalWithoutTransientProperties(external);
    result.updateTransientProperties();
    return result;
  }

  @Mapping(target = "filteredTransactionList", ignore = true)
  WorkspaceModel toInternalWithoutTransientProperties(ExternalWorkspace external);

  default ObservableList<TransactionModel> toObservableTransactionList(
      List<ExternalTransaction> external) {
    return FXCollections.observableArrayList(
        external.stream().map(this::toInternal).collect(toList()));
  }

  default TransactionModel toInternal(ExternalTransaction external) {
    var result = toInternalWithoutTransientProperties(external);
    result.updateTransientProperties();
    return result;
  }

  @Mapping(target = "lowercaseWords", ignore = true)
  TransactionModel toInternalWithoutTransientProperties(ExternalTransaction external);

  default ObservableList<CategoryModel> toObservableCategoryList(List<ExternalCategory> external) {
    if (external == null) {
      return FXCollections.observableArrayList();
    }
    return FXCollections.observableArrayList(
        external.stream().map(this::toInternal).collect(toList()));
  }

  @Mapping(target = "categoryIconName", expression = "java(internal.getCategoryIcon().name())")
  ExternalCategory toExternal(CategoryModel internal);

  @Mapping(
      target = "categoryIcon",
      expression = "java(CategoryIcon.valueOf(external.getCategoryIconName()))")
  CategoryModel toInternal(ExternalCategory external);

  static ExternalWorkspaceMapper create() {
    return Mappers.getMapper(ExternalWorkspaceMapper.class);
  }
}
