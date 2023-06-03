package nl.marisabel.util;

public enum ExpenseEntityEnum {
    ENTITY1("Entity1", "Category1"),
    ENTITY2("Entity2", "Category2"),
    ENTITY3("Entity3", "Category3"),
    // Add more entities and categories here
    ;

    private final String entity;
    private final String category;

    ExpenseEntityEnum(String entity, String category) {
        this.entity = entity;
        this.category = category;
    }

    public String getEntity() {
        return entity;
    }

    public String getCategory() {
        return category;
    }

    public static String getCategoryByEntity(String entity) {
        for (ExpenseEntityEnum value : ExpenseEntityEnum.values()) {
            if (value.entity.equalsIgnoreCase(entity)) {
                return value.category;
            }
        }
        return null; // Return null or handle the case when entity is not found
    }
}
