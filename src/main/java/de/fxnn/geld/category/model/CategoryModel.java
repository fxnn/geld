package de.fxnn.geld.category.model;

import de.fxnn.geld.common.model.Model;
import de.fxnn.geld.io.ikonli.CategoryIcon;
import de.fxnn.geld.transaction.model.FilterParser;
import de.fxnn.geld.transaction.model.TransactionModel;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Data;
import org.kordamp.ikonli.Ikon;

/**
 * Categories are used to group transactions. Every transaction can have zero or one category. They
 * should be easily recognizable by the user and are therefore represented through an icon. They
 * should be easy to manage, which is why users can't manually add or remove transactions to
 * categories, but simply specify filter expressions.
 */
@Data
public class CategoryModel implements Model {

  /** Determines which transactions belong to this category. */
  private String filterExpression;

  /** Transient property representing the parsed {@link #filterExpression}. */
  private Predicate<TransactionModel> filterPredicate;

  private CategoryIcon categoryIcon;

  @Override
  public void updateTransientProperties() {
    this.filterPredicate = new FilterParser().parse(filterExpression);
  }

  public Ikon getIkon() {
    return categoryIcon.getIkon();
  }

  public void appendFilterExpression(String filterExpression) {
    this.filterExpression =
        (Optional.ofNullable(this.filterExpression).orElse("") + " " + filterExpression).trim();
  }
}
