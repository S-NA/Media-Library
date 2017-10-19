package medialibrary;

import java.io.Serializable;
import java.util.Objects;

public class Media implements Comparable<Media>, Serializable {

  private static final long serialVersionUID = 1L;
  private String title;
  private String format;

  /**
   * Create a non-loaned media object.
   *
   * @param title
   *          media's title
   * @param format
   *          media's format
   */
  public Media(String title, String format) {
    super();
    this.title = title;
    this.format = format;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public int compareTo(Media item) {
    return title.compareTo(item.getTitle());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Media other = (Media) obj;

    return Objects.equals(this.title, other.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title);
    /* add ', format' if you want sanity, and update equals() */
  }

  @Override
  public String toString() {
    return title + " - " + format;
  }

}
