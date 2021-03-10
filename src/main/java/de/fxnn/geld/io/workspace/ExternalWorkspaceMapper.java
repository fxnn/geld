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

  @Mapping(target = "filteredTransactionList", ignore = true)
  WorkspaceModel toInternal(ExternalWorkspace external);

  default ObservableList<TransactionModel> toInternalTransaction(
      List<ExternalTransaction> external) {
    return FXCollections.observableList(external.stream().map(this::toInternal).collect(toList()));
  }

  @Mapping(target = "lowercaseWords", ignore = true)
  TransactionModel toInternal(ExternalTransaction external);

  static ExternalWorkspaceMapper create() {
    return Mappers.getMapper(ExternalWorkspaceMapper.class);
  }
}
