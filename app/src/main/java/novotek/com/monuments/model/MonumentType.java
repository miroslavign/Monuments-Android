/*
 * MonumentType.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 5/23/2016
 * Copyright (c) 2016 Novotek All rights reserved.
 */

package novotek.com.monuments.model;

public class MonumentType {
    public Long id;
    public String typeName;

    public MonumentType(){}

    public MonumentType(String name) {
        this.typeName = name;
    }
}
