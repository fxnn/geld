package de.fxnn.geld.io.ikonli;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.unicons.UniconsLine;

public enum LabelIcon {
  BABY(UniconsLine.BABY_CARRIAGE),
  BALL(UniconsLine.DRIBBBLE),
  BAR(UniconsLine.GLASS_MARTINI_ALT),
  BOAT(UniconsLine.ANCHOR),
  BOOKS(UniconsLine.BOOKS),
  BUS(UniconsLine.BUS),
  CAR(UniconsLine.CAR),
  CASH(UniconsLine.MONEY_BILL),
  CHILDREN(UniconsLine.KID),
  CREDIT_CARD(UniconsLine.CREDIT_CARD),
  DRINK(UniconsLine.GLASS),
  DRUGSTORE(UniconsLine.TOILET_PAPER),
  EDUCATION(UniconsLine.BOOK_READER),
  ENERGY(UniconsLine.LIGHTBULB),
  FAST_FOOD(UniconsLine.PIZZA_SLICE),
  FINANCE(UniconsLine.UNIVERSITY),
  FOOD(UniconsLine.UTENSILS),
  FUEL(UniconsLine.PUMP),
  FUN(UniconsLine.BOWLING_BALL),
  GARDEN(UniconsLine.SHOVEL),
  GIFT(UniconsLine.GIFT),
  HEALTH(UniconsLine.HEART_MEDICAL),
  HOME(UniconsLine.HOME),
  HOTEL(UniconsLine.BED),
  INTERNET(UniconsLine.WIFI),
  MAIL(UniconsLine.ENVELOPE),
  MEDICAL(UniconsLine.HOSPITAL),
  MEIDCINE(UniconsLine.CAPSULE),
  MUSIC(UniconsLine.MUSIC),
  NATURE(UniconsLine.TREES),
  PLANE(UniconsLine.PLANE),
  SECURITY(UniconsLine.LOCK_ALT),
  SHOPPING_OFFLINE(UniconsLine.SHOPPING_BAG),
  SHOPPING_ONLINE(UniconsLine.BOX),
  SUPERMARKET(UniconsLine.SHOPPING_CART),
  TABLE_TENNIS(UniconsLine.TABLE_TENNIS),
  TELEPHONE(UniconsLine.PHONE),
  TELEVISION(UniconsLine.TV_RETRO),
  TOOLS(UniconsLine.WRENCH),
  TRAIN(UniconsLine.METRO),
  TRASH(UniconsLine.TRASH_ALT),
  TRAVEL(UniconsLine.MAP),
  WINTER(UniconsLine.SNOWFLAKE),
  WORK(UniconsLine.BAG);

  private final Ikon ikon;

  LabelIcon(Ikon ikon) {
    this.ikon = ikon;
  }

  public Ikon getIkon() {
    return ikon;
  }
}
