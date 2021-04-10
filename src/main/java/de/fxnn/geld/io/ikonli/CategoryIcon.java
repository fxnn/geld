package de.fxnn.geld.io.ikonli;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.unicons.UniconsLine;

/**
 * @see <a href="https://kordamp.org/ikonli/cheat-sheet-unicons.html">List of all supported
 *     icons</a>
 */
public enum CategoryIcon {
  ANIMAL(UniconsLine.GITHUB_ALT),
  BABY(UniconsLine.BABY_CARRIAGE),
  BALL(UniconsLine.DRIBBBLE),
  BAR(UniconsLine.GLASS_MARTINI_ALT),
  BOAT(UniconsLine.ANCHOR),
  BOOKS(UniconsLine.BOOKS),
  BUS(UniconsLine.BUS),
  CAR(UniconsLine.CAR),
  CASH(UniconsLine.MONEY_BILL),
  CHILDREN(UniconsLine.KID),
  CLEANING(UniconsLine.SANITIZER),
  CLOUD(UniconsLine.CLOUD),
  COFFEE(UniconsLine.COFFEE),
  COMPUTER(UniconsLine.DESKTOP),
  CONSTRUCTION(UniconsLine.CONSTRUCTOR),
  CREDIT_CARD(UniconsLine.CREDIT_CARD),
  DOLLAR(UniconsLine.DOLLAR_SIGN),
  DRINK(UniconsLine.GLASS),
  EDUCATION(UniconsLine.BOOK_READER),
  ENERGY(UniconsLine.LIGHTBULB),
  FAST_FOOD(UniconsLine.PIZZA_SLICE),
  FILM(UniconsLine.FILM),
  FINANCE(UniconsLine.UNIVERSITY),
  FLOWER(UniconsLine.FLOWER),
  FOOD(UniconsLine.UTENSILS),
  FUEL(UniconsLine.PUMP),
  FUN(UniconsLine.BOWLING_BALL),
  GARDEN(UniconsLine.SHOVEL),
  GIFT(UniconsLine.GIFT),
  HEALTH(UniconsLine.HEART_MEDICAL),
  HYGIENE(UniconsLine.TOILET_PAPER),
  HOME(UniconsLine.HOME),
  HOTEL(UniconsLine.BED),
  INSURANCE(UniconsLine.SHIELD),
  INTERNET(UniconsLine.WIFI),
  MAIL(UniconsLine.ENVELOPE),
  MEDIA(UniconsLine.PLAY_CIRCLE),
  MEDICAL(UniconsLine.HOSPITAL),
  MEDICINE(UniconsLine.CAPSULE),
  MOVE(UniconsLine.ARROWS_H),
  MOBILE_PHONE(UniconsLine.MOBILE_ANDROID),
  MUSIC(UniconsLine.MUSIC),
  NATURE(UniconsLine.TREES),
  PAPER(UniconsLine.COPY),
  PEN(UniconsLine.PEN),
  PLANE(UniconsLine.PLANE),
  SECURITY(UniconsLine.LOCK_ALT),
  SHOPPING_OFFLINE(UniconsLine.SHOPPING_BAG),
  SHOPPING_ONLINE(UniconsLine.BOX),
  SMILE(UniconsLine.GRIN),
  SUN(UniconsLine.SUN),
  SUPERMARKET(UniconsLine.SHOPPING_CART),
  TABLE_TENNIS(UniconsLine.TABLE_TENNIS),
  TELEPHONE(UniconsLine.PHONE),
  TELEVISION(UniconsLine.TV_RETRO),
  TOOLS(UniconsLine.WRENCH),
  TRAIN(UniconsLine.METRO),
  TRASH(UniconsLine.TRASH_ALT),
  TRAVEL(UniconsLine.MAP),
  UMBRELLA(UniconsLine.UMBRELLA),
  WINTER(UniconsLine.SNOWFLAKE),
  WORK(UniconsLine.BAG);

  private final Ikon ikon;

  CategoryIcon(Ikon ikon) {
    this.ikon = ikon;
  }

  public Ikon getIkon() {
    return ikon;
  }
}
