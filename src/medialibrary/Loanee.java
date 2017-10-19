package medialibrary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Loanee implements Serializable, Comparable<Loanee> {

  private static final long serialVersionUID = 1L;
  private String name;
  private Date dateLoaned;

  public Loanee(String name, Date dateLoaned) {
    this.name = name;
    this.dateLoaned = dateLoaned;
  }

  public Loanee() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDateLoaned() {
    return dateLoaned;
  }

  public void setDateLoaned(Date dateLoaned) {
    this.dateLoaned = dateLoaned;
  }

  public boolean isEmpty() {
    return (name == null || dateLoaned == null);
  }

  public boolean clear() {
    if (isEmpty()) {
      return false;
    }
    name = null;
    dateLoaned = null;
    return true;
  }

  @Override
  public int compareTo(Loanee o) {
    if (!this.isEmpty() && !o.isEmpty()) {
      return o.getDateLoaned().compareTo(dateLoaned);
    } else if (this.isEmpty() && o.isEmpty()) {
      return 0;
    } else if (dateLoaned == null) {
      return 1;
    } else if (o.getDateLoaned() == null) {
      return -1;
    } else {
      return 0;
    }
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
    final Loanee other = (Loanee) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.dateLoaned, other.dateLoaned)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, dateLoaned);
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "";
    } else {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      return '(' + name + " on " + df.format(dateLoaned) + ')';
    }
  }
}
