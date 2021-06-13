package com.rezerve_sepeti;

public class Place {
    private String name;
    private String uuid;
    private String type;
    private int tablePcs;
    public Place(String name,String uuid, String type, int tablePcs) {
        this.name = name;
        this.type = type;
        this.tablePcs = tablePcs;
        this.uuid = uuid;//TODO: Silebilirim
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public int getTablePcs() {
        return tablePcs;
    }
}