package novotek.com.monuments.model;

/**
 * Created by BX on 5/23/2016.
 */
public class MonumentType {
    public Long id;
    public String typeName;

    public MonumentType(){}

    public MonumentType(String name) {
        this.typeName = name;
    }
}
