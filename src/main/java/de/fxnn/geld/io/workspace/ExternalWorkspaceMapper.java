package de.fxnn.geld.io.workspace;

import static java.util.stream.Collectors.toList;

import de.fxnn.geld.jfx.model.TransactionModel;
import de.fxnn.geld.jfx.model.WorkspaceModel;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {FXCollections.class, Collectors.class})
public interface ExternalWorkspaceMapper {

  ExternalWorkspace toExternal(WorkspaceModel internal);

  default WorkspaceModel toInternal(ExternalWorkspace external) {
    var result = toInternalWithoutTransientProperties(external);
    result.updateTransientProperties();
    return result;
  }

  @Mapping(target = "filteredTransactionList", ignore = true)
  WorkspaceModel toInternalWithoutTransientProperties(ExternalWorkspace external);

  default ObservableList<TransactionModel> toInternalTransaction(
      List<ExternalTransaction> external) {
    return FXCollections.observableList(external.stream().map(this::toInternal).collect(toList()));
  }

  default TransactionModel toInternal(ExternalTransaction external) {
    var result = toInternalWithoutTransientProperties(external);
    result.updateTransientProperties();
    return result;
  }

  @Mapping(target = "lowercaseWords", ignore = true)
  TransactionModel toInternalWithoutTransientProperties(ExternalTransaction external);

  static ExternalWorkspaceMapper create() {
    return Mappers.getMapper(ExternalWorkspaceMapper.class);
  }
}
