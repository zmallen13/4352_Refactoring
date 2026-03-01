import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String _name;
    private List<Rental> _rentals = new ArrayList<>();
    
    public Customer (String name) {
        _name = name;
    }
    
    public void addRental(Rental arg) {
        _rentals.add(arg);
    }
    
    public String getName() {
        return _name;
    }
    
    public String statementXml() {
        StringBuilder result = new StringBuilder();
        result.append("<customer>\n");
        result.append("  <name>").append(getName()).append("</name>\n");
        result.append("  <rentals>\n");

        for (Rental each : _rentals) {
            result.append("    <rental>\n");
            result.append("      <movie>")
                  .append(each.getMovie().getTitle())
                  .append("</movie>\n");
            result.append("    </rental>\n");
        }

        result.append("  </rentals>\n");
        result.append("</customer>");

        return result.toString();
    }
}